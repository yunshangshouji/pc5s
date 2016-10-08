package zhuboss.pc2server.sengine.clientserver.encode;

import java.util.List;

import zhuboss.pc2server.common.bufparser.MyByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequestEncoder;

public class MyHttpRequestEncoder extends HttpRequestEncoder {

	
	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		return msg instanceof MyHttpObject;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg,
			List<Object> out) throws Exception {
		//由于只有 一条线路，msg只能
		MyHttpObject myHttpMessage = (MyHttpObject) msg;
		if(myHttpMessage.getMessage() instanceof HttpObject){
			super.encode(ctx, myHttpMessage.getMessage(), out);
			for(int i=0;i<out.size();i++){
				out.set(i, new MyByteBuf(myHttpMessage.getUuidBytes(),myHttpMessage.getZipFlag(),(ByteBuf)out.get(i)));
			}
		}else{
			//WebSocket
			out.add(new MyByteBuf(myHttpMessage.getUuidBytes(),myHttpMessage.getZipFlag(),(ByteBuf)myHttpMessage.getMessage()));
		}
	}
	
}
