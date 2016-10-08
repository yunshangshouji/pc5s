package zhuboss.pc2server.sengine.webserver.bytebuf2response;

import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpResponse;

/**
 * 过滤HTTP1.0，不支持gzip压缩<br>
 * 尚未解决，连接断开的问题，http1.0好像是通过连接断开来表示请求结束
 * @author Administrator
 *
 */
public class MyHttpContentCompressor extends HttpContentCompressor {
	/**
	 * HttpContent分段提交，记录状态
	 */
	boolean gzip = true;
	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		if(gzip == false){
			return false;
		}
		//不支持http1.0压缩
		if(msg instanceof HttpResponse && ((HttpResponse)msg).protocolVersion().minorVersion()==0){
			gzip = false;
			return false;
		}
		return super.acceptOutboundMessage(msg);
	}

	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		return super.acceptInboundMessage(msg);
	}
	
}
