package zhuboss.pc2server.sengine.webserver.bytebuf2response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.pc2server.common.JavaUtil;
import zhuboss.pc2server.common.bufparser.MyByteBuf;
import zhuboss.pc2server.sengine.StartSEngine;
import zhuboss.pc2server.sengine.clientserver.ChannelInfo;
import zhuboss.pc2server.sengine.clientserver.Server4ClientRegister;
import zhuboss.pc2server.sengine.proxycache.CacheManager;
import zhuboss.pc2server.sengine.webserver.Server4WwwHandler;

public class BytebufToHttpResponseEncoder extends ChannelHandlerAdapter{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	MyHttpResponseDecoder  myHttpResponseDecoder = new MyHttpResponseDecoder();
	private String sengineDomain;
	static final CharSequence LOCATION = "Location";
	
	public BytebufToHttpResponseEncoder(String sengineDomain){
		this.sengineDomain = sengineDomain;
	}
	
	 public boolean acceptOutboundMessage(Object msg) throws Exception {
	        return msg instanceof ByteBuf;
	    }
	 
	 @Override
	   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
	        RecyclableArrayList out = null;
	        try {
	            if (acceptOutboundMessage(msg)) {
	                out = RecyclableArrayList.newInstance();
	                @SuppressWarnings("unchecked")
	                ByteBuf cast = (ByteBuf) msg;
	                
	                /**
	                 *  record to buffer for proxy_cache
	                 *  by zhu
	                 */
	                Server4WwwHandler server4WwwHandler = ctx.channel().pipeline().get(Server4WwwHandler.class);
	                if(server4WwwHandler.getCacheKey()!=null){
	                	ByteBuf dup = cast.duplicate(); //just view
	                	dup.readBytes(ctx.channel().pipeline().get(Server4WwwHandler.class).getBaos(), cast.readableBytes());
	                }
	                
	                //
	                encode(ctx, cast, out);
	                
	                //add by zhu
	                if(server4WwwHandler.getCacheKey()!=null && out.get(0) instanceof HttpMessage){ //提炼出last_modified
	                	CharSequence lastModifiedHeader = ((HttpMessage)out.get(0)).headers().get(HttpHeader.LAST_MODIFIED.asString()) ;
	                	long lastModified = CacheManager.getDateField(lastModifiedHeader==null?null:lastModifiedHeader.toString());
	                	server4WwwHandler.setLastModified(lastModified==-1? new Date().getTime():lastModified);
	                }
	                
	                if(server4WwwHandler.getCacheKey()!=null && !out.isEmpty() && out.get(out.size()-1) instanceof LastHttpContent){
	                	CacheManager cacheManager = StartSEngine.getApplicationContext().getBean(CacheManager.class);
	                	cacheManager.addCache(server4WwwHandler.getUserDomain(), server4WwwHandler.getCacheKey(), server4WwwHandler.getUri(),server4WwwHandler.getLastModified(), server4WwwHandler.getBaos().toByteArray());
	                }
	                
	                
	                if (out.isEmpty()) {
	                    out.recycle();
	                    out = null;
//removed by Zhu,my data is little
//	                    throw new EncoderException(
//	                            StringUtil.simpleClassName(this) + " must produce at least one message.");
	                }
	            } else {
	                ctx.write(msg, promise);
	            }
	        } catch (EncoderException e) {
	            throw e;
	        } catch (Throwable t) {
	            throw new EncoderException(t);
	        } finally {
	            if (out != null) {
	                final int sizeMinusOne = out.size() - 1;
	                if (sizeMinusOne == 0) {
	                    ctx.write(out.get(0), promise);
	                } else if (sizeMinusOne > 0) {
	                    // Check if we can use a voidPromise for our extra writes to reduce GC-Pressure
	                    // See https://github.com/netty/netty/issues/2525
	                    ChannelPromise voidPromise = ctx.voidPromise();
	                    boolean isVoidPromise = promise == voidPromise;
	                    for (int i = 0; i < sizeMinusOne; i ++) {
	                        ChannelPromise p;
	                        if (isVoidPromise) {
	                            p = voidPromise;
	                        } else {
	                            p = ctx.newPromise();
	                        }
	                        ctx.write(out.get(i), p);
	                    }
	                    ctx.write(out.get(sizeMinusOne), promise);
	                }
	                out.recycle();
	            }
	        }
	    }
	 
	
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		out.addAll(this.myChannelRead(ctx, msg)); //该方法不应该更改msg.refCnt
		for(int i=0;i<out.size();i++){
			if(logger.isDebugEnabled()){
				logger.debug("encode result:" + JavaUtil.bytes2UUID(((MyByteBuf)msg).getUuidBytes()) + out.get(i));
			}
			if(out.get(i) instanceof DefaultHttpResponse){
				
				/**
				 * proxy_cache
				 * 非200类请求不做cache
				 */
				Server4WwwHandler server4WwwHandler = ctx.channel().pipeline().get(Server4WwwHandler.class);
                if(server4WwwHandler.getCacheKey()!=null && ((DefaultHttpResponse)out.get(i)).status().code() != 200){
                	server4WwwHandler.setCacheKey(null);
                }
                
				HttpHeaders httpHeaders = ((DefaultHttpResponse)out.get(i)).headers();
				if(httpHeaders.contains(LOCATION)){//重定向
					String location = httpHeaders.get(LOCATION).toString();
					String userDomain = ctx.channel().pipeline().get(Server4WwwHandler.class).getUserDomain();
					ChannelInfo channelInfo = Server4ClientRegister.getInstance().getChannelInfo(userDomain);
					if(channelInfo!=null && location.contains(channelInfo.getHost().split(":")[0])){ //针对本地服务的重写向，要改域名，其它地方不重写
						location = location.replaceFirst("//[^:^/]+(:[0-9]+)?", "//"+((MyByteBuf)msg).getUserDomain()+ sengineDomain);
						httpHeaders.set(LOCATION, location);
					}
				}
			}
		}
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}


	/**
	 * 自来代码来自HtppResponseDecoder
	 */
	 ByteBuf cumulation;
	    private Cumulator cumulator = MERGE_CUMULATOR;
	    private boolean singleDecode;
	    private boolean first;
	public List<Object> myChannelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		List<Object> out = new ArrayList<Object>();
        if (msg instanceof ByteBuf) {
            try {
                ByteBuf data = (ByteBuf) msg;
                first = cumulation == null;
                if (first) {
                    cumulation = data;
                } else {
                    cumulation = cumulator.cumulate(ctx.alloc(), cumulation, data);
                }
                callDecode(ctx, cumulation, out);
            } catch (DecoderException e) {
                throw e;
            } catch (Throwable t) {
            	logger.error(t.getMessage(),t);
                throw new DecoderException(t);
            } finally {
                if (cumulation != null && !cumulation.isReadable() ) { //本次接收的字节数据全部parse完，即header完或body完
                	cumulation.release();
                    cumulation = null;
                }
                
            }
        } 
        return out;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (cumulation != null && !first && cumulation.refCnt() == 1) {
            // discard some bytes if possible to make more room in the
            // buffer but only if the refCnt == 1  as otherwise the user may have
            // used slice().retain() or duplicate().retain().
            //
            // See:
            // - https://github.com/netty/netty/issues/2327
            // - https://github.com/netty/netty/issues/1764
            cumulation.discardSomeReadBytes();
        }
        ctx.fireChannelReadComplete();
    }
    public boolean isSingleDecode() {
        return singleDecode;
    }
    protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            while (in.isReadable()) {
                int outSize = out.size();
                int oldInputLength = in.readableBytes();
                myHttpResponseDecoder.decode(ctx, in, out);

                // Check if this handler was removed before continuing the loop.
                // If it was removed, it is not safe to continue to operate on the buffer.
                //
                // See https://github.com/netty/netty/issues/1664
                if (ctx.isRemoved()) {
                    break;
                }

                if (outSize == out.size()) {
                    if (oldInputLength == in.readableBytes()) {
                        break;
                    } else {
                        continue;
                    }
                }

                if (oldInputLength == in.readableBytes()) {
                    throw new DecoderException(
                            StringUtil.simpleClassName(getClass()) +
                            ".decode() did not read anything but decoded a message.");
                }

                if (isSingleDecode()) {
                    break;
                }
            }
        } catch (DecoderException e) {
            throw e;
        } catch (Throwable cause) {
            throw new DecoderException(cause);
        }
    }
    
    public static final Cumulator MERGE_CUMULATOR = new Cumulator() {
        @Override
        public ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in) {
            ByteBuf buffer;
            if (cumulation.writerIndex() > cumulation.maxCapacity() - in.readableBytes()
                    || cumulation.refCnt() > 1) {
                // Expand cumulation (by replace it) when either there is not more room in the buffer
                // or if the refCnt is greater then 1 which may happen when the user use slice().retain() or
                // duplicate().retain().
                //
                // See:
                // - https://github.com/netty/netty/issues/2327
                // - https://github.com/netty/netty/issues/1764
                buffer = expandCumulation(alloc, cumulation, in.readableBytes());
            } else {
                buffer = cumulation;
            }
            buffer.writeBytes(in);
// By Zhu           
            in.release();
            return buffer;
        }
    };
    static ByteBuf expandCumulation(ByteBufAllocator alloc, ByteBuf cumulation, int readable) {
        ByteBuf oldCumulation = cumulation;
        cumulation = alloc.buffer(oldCumulation.readableBytes() + readable);
        cumulation.writeBytes(oldCumulation);
        oldCumulation.release();
        return cumulation;
    }

    /**
     * Cumulate {@link ByteBuf}s.
     */
    public interface Cumulator {
        /**
         * Cumulate the given {@link ByteBuf}s and return the {@link ByteBuf} that holds the cumulated bytes.
         * The implementation is responsible to correctly handle the life-cycle of the given {@link ByteBuf}s and so
         * call {@link ByteBuf#release()} if a {@link ByteBuf} is fully consumed.
         */
        ByteBuf cumulate(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf in);
    }
}