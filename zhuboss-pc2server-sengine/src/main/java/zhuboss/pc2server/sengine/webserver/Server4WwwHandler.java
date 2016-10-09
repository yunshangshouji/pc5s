package zhuboss.pc2server.sengine.webserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.ReferenceCountUtil;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import zhuboss.framework.spring.CustomizedPropertyPlaceholderConfigurer;
import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.JavaUtil;
import zhuboss.pc2server.common.bufparser.TransPackage;
import zhuboss.pc2server.sengine.StartSEngine;
import zhuboss.pc2server.sengine.clientserver.ChannelInfo;
import zhuboss.pc2server.sengine.clientserver.Server4ClientRegister;
import zhuboss.pc2server.sengine.clientserver.TranficStatis;
import zhuboss.pc2server.sengine.clientserver.encode.MyHttpObject;
import zhuboss.pc2server.sengine.mis.service.UserService;
import zhuboss.pc2server.sengine.proxycache.CacheManager;
import zhuboss.pc2server.sengine.proxycache.LRUElement;
import zhuboss.pc2server.sengine.webserver.pages.PageDispatcher;
import zhuboss.pc2server.sengine.webserver.sessionmanager.CookieSessionManager;
import zhuboss.pc2server.sengine.webserver.sessionmanager.MyFullHttpRequestWrapper;
public class Server4WwwHandler extends ChannelHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	static final CharSequence HOST_NAME = "Host";
	
	private String userDomain;
	private String cacheKey;
	private ByteArrayOutputStream baos;
	private String uri;
	private long lastModified = -1;
	/**
	 *  每当从客户端收到新的数据时，
     * 这个方法会在收到消息时被调用，
     * 这个例子中，收到的消息的类型是ByteBuf

	 */
	public long getFlowLimit(){
		UserService userService = StartSEngine.getApplicationContext().getBean(UserService.class);
		return userService.getFlowLimit(userDomain);
	}
	
	public AccessCfg getAccessCfg(){
		UserService userService = StartSEngine.getApplicationContext().getBean(UserService.class);
		return userService.getAccessCfg(userDomain);
	}
	
	public Integer getUserId(){
		UserService userService = StartSEngine.getApplicationContext().getBean(UserService.class);
		return userService.getUserId(userDomain);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, final Object msg) {
		try {
			if (msg instanceof FullHttpRequest) { //HttpRequest、HttpContent
				//reset variables
				userDomain = null;
				cacheKey = null;
				baos = null;
				uri = null;
				lastModified = -1;
				//
				FullHttpRequest req = (FullHttpRequest)msg;

				/**
				 * 支持 WebSocket，只传输不解析
				 */
				if(req.headers().contains("Upgrade", "websocket")){
					ctx.channel().pipeline().remove("RequestDecoder");
					ctx.channel().pipeline().remove("RequestAggregator");
					ctx.channel().pipeline().remove("ResponseEncoder");
					ctx.channel().pipeline().remove("BytebufToHttpResponseEncoder");
				}
				
				
				if(req.decoderResult().isFailure()){//bad request, close connection
					ctx.channel().close();
					return;
				}
				MyFullHttpRequestWrapper reqWrapper = new MyFullHttpRequestWrapper(req);
				userDomain = reqWrapper.getUserDomain();
				if(userDomain == null){ //bad request
					logger.warn("from "+ctx.channel().remoteAddress());
					String errmsg = req.headers().get(HOST_NAME).toString();
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("errmsg", HOST_NAME+":"+errmsg);
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"bad_request.ftl",data);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				
				if(req.uri().equals("/robots.txt")){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"robots.ftl",null);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				Integer userId = this.getUserId();
				if(userId==null){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"domain_not_exists.ftl",null);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				long tranficSize = TranficStatis.getInstance().get(userId);
				if (tranficSize> this.getFlowLimit() ){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					Map<String,Object> data = new HashMap<String,Object>();
					data.put("flowLimit", this.getFlowLimit()/1024/1024);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"flow_limit.ftl",data);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				
				
				logger.info(userDomain+","+req.uri());
				/**
				 * auth
				 */
				int auth = auth(ctx,reqWrapper);
				if(auth==AUTH_TO_AUTH){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					Map<String,Object> data = new HashMap<String,Object>();
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"to_auth.ftl",data);
					ctx.channel().writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}else if(auth == AUTH_NOT_ALLOWED){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"not_allowed.ftl",null);
					ctx.channel().writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}else if(auth == AUTH_LOGGED){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					Headers<CharSequence> headers = new DefaultHttpHeaders();
					String rootDomain = (String)StartSEngine.getApplicationContext().getBean(CustomizedPropertyPlaceholderConfigurer.class).getContextProperty("sengine.domain");
					headers.add("Location", "http://"+this.userDomain+rootDomain+"/");
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.FOUND,headers,null,null);
					StartSEngine.getApplicationContext().getBean(CookieSessionManager.class).writeCookieToken(response, userDomain);
					ctx.channel().writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				
				
				/**
				 * 缓存策略
				 */
				CacheManager cacheManager = StartSEngine.getApplicationContext().getBean(CacheManager.class);
				String destCacheKey = cacheManager.getCacheKey(userDomain, reqWrapper.getPath(), reqWrapper.getRawQuery());
				if(destCacheKey!=null){
					LRUElement element =  cacheManager.getCacheElement(userDomain, destCacheKey);
					if(element!=null){//exists
						if(cacheManager.isNotModified(userDomain, destCacheKey, req.headers())){
							PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
							DefaultFullHttpResponse response = pageDispatcher.dispatch(HttpResponseStatus.NOT_MODIFIED,null);
							ctx.channel().writeAndFlush(response);
							ReferenceCountUtil.release(msg);
						}else{
							ByteBuf  content = ctx.alloc().buffer(element.getBytes());
							cacheManager.loadFile(userDomain, destCacheKey,content);
							ctx.channel().writeAndFlush(content);
						}
						return ;
					}else{
						//will load
						//清空头的部标识，避免返回304无法缓存
						req.headers().remove(HttpHeader.IF_MODIFIED_SINCE.asString());
						req.headers().remove(HttpHeader.LAST_MODIFIED.asString());
						req.headers().remove(HttpHeader.IF_MODIFIED_SINCE.asString());
						req.headers().remove(HttpHeader.IF_UNMODIFIED_SINCE.asString());
						req.headers().remove(HttpHeader.IF_NONE_MATCH.asString());
						this.cacheKey = destCacheKey;
						baos = new ByteArrayOutputStream();
						uri = req.uri();
					}
				}
				
				ChannelInfo clientChannelInfo = Server4ClientRegister.getInstance().getChannelInfo(userDomain);
				if(clientChannelInfo == null){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"no_client.ftl",null);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				//TODO
				req.headers().set(HOST_NAME, clientChannelInfo.getHost());
				//注意，不即时flush，将造成响应缓慢
				byte zipFlag = req.uri().matches(".*(htm|html|css|js)")?Constants.ZIP_FLAG_YES:Constants.ZIP_FLAG_NO;
				MyHttpObject myHttpObject = new MyHttpObject(JavaUtil.uuid2Bytes(Server4WwwConManager.getInstance().getUUID(ctx)),zipFlag, (HttpObject)msg);
				//由于writeAndFlush参数可能HttpObject是ByteBuf对象，涉及父类对其进行释放问题，必须等writeAndFlush执行结束
				ChannelPromise channelPromise = clientChannelInfo.getChannel().newPromise();
				channelPromise.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						if(!future.isSuccess()){
							ReferenceCountUtil.release(msg);
						}
					}
				});
				clientChannelInfo.getChannel().writeAndFlush(myHttpObject, channelPromise);
				
	        }else if (msg instanceof ByteBuf) {//for WebSocket mode
	        	ChannelInfo clientChannelInfo = Server4ClientRegister.getInstance().getChannelInfo(userDomain);
				if(clientChannelInfo == null){
					PageDispatcher pageDispatcher = StartSEngine.getApplicationContext().getBean(PageDispatcher.class);
					DefaultFullHttpResponse response = pageDispatcher.buildHttpResponse(ctx,HttpResponseStatus.OK,null,"no_client.ftl",null);
					ctx.writeAndFlush(response);
					ReferenceCountUtil.release(msg);
					return;
				}
				MyHttpObject myHttpObject = new MyHttpObject(JavaUtil.uuid2Bytes(Server4WwwConManager.getInstance().getUUID(ctx)),Constants.ZIP_FLAG_NO, msg);
	        	clientChannelInfo.getChannel().writeAndFlush(myHttpObject);
	        }
	        else{
	        	throw new RuntimeException("not support:" + msg); 
	        }
	        
			if(logger.isDebugEnabled()){
				logger.debug(msg.toString());
			}
		}catch(Throwable t){
			logger.error(t.getMessage(),t);
			try {
				throw t;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}finally {
			/**
	         * ByteBuf是一个引用计数对象，这个对象必须显示地调用release()方法来释放。
	         * 请记住处理器的职责是释放所有传递到处理器的引用计数对象。
	         */
			//所有handler接收ByteBuf都要释放，encoder、decoder是父类中写死的,
//			ReferenceCountUtil.release(msg); //ByteBuf的最后使用者要负责释放

		}

	}
	
	/**
	 * 正常通过
	 */
	private static final int AUTH_PASS = 0;
	/**
	 * 请求为登录成功
	 */
	private static final int AUTH_LOGGED = 1;
	/**
	 * 需要登录
	 */
	private static final int AUTH_TO_AUTH = 2;
	/**
	 * 无权访问(仅支持white IP)
	 */
	private static final int AUTH_NOT_ALLOWED = 3;
	
	private int auth(ChannelHandlerContext ctx,MyFullHttpRequestWrapper reqWrapper){
		
		//未配置，默认不需要校验
		AccessCfg accessCfg = this.getAccessCfg();
		if(!StringUtils.hasText(accessCfg.getWhiteIpList()) && !StringUtils.hasText(accessCfg.getWebAccessPwd())){
			return AUTH_PASS;
		}

		/**
		 * 是否已经登录
		 */
		String userDomain = reqWrapper.getUserDomain();
		if (StartSEngine.getApplicationContext().getBean(CookieSessionManager.class).hasToken(reqWrapper,userDomain)){
			return AUTH_PASS;
		}
		
		
		//白名单
		String whiteIpList = accessCfg.getWhiteIpList();
		if(StringUtils.hasText(accessCfg.getWhiteIpList())){
			String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getHostString();
			if(whiteIpList.indexOf(ip)>-1){
				return AUTH_PASS;
			}
		}
		
		//密码
		
		if(StringUtils.hasText(accessCfg.getWebAccessPwd())){
			if(accessCfg.getWebAccessPwd().equals(reqWrapper.getQueryParams().get("webAccessPwd"))){
				return AUTH_LOGGED;
			}else{
				return AUTH_TO_AUTH;
			}
		}else{
			return AUTH_NOT_ALLOWED;
		}
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.debug("channelReadComplete");
//		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.warn(cause.getClass()+","+cause.getMessage());
		if(logger.isDebugEnabled()){
			cause.printStackTrace();
		}
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		logger.info("channelRegistered:"+ctx.channel().remoteAddress());
		Server4WwwConManager.getInstance().register(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		try{
			if(userDomain == null){//未接收到请求
				logger.warn("no do connect:" +ctx.channel().remoteAddress() );
				return;
			}
			ChannelInfo clientChannelInfo = Server4ClientRegister.getInstance().getChannelInfo(userDomain);
			if(clientChannelInfo==null){
				logger.warn("no client connect:" );
				return ;
			}
			byte[] uuidBytes = JavaUtil.uuid2Bytes(Server4WwwConManager.getInstance().getUUID(ctx));
			TransPackage transPackage = new TransPackage(uuidBytes, Constants.OPERATE_TYPE_DISCONNECT,Constants.ZIP_FLAG_NO, new byte[]{});
			clientChannelInfo.getChannel().writeAndFlush(transPackage);
			if(logger.isDebugEnabled()){
				logger.debug("channelUnregistered transPackage:"+JavaUtil.bytesToHexString(transPackage.getFullBytes()));
			}
			Server4WwwConManager.getInstance().unRegister(ctx);
		}finally{
			super.channelUnregistered(ctx);
		}
	}

	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		try{
			super.close(ctx, promise);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getUserDomain() {
		return userDomain;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public ByteArrayOutputStream getBaos() {
		return baos;
	}

	public String getUri() {
		return uri;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	
	
}
