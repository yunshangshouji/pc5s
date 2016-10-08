package org.zhuboss.pc2server.pcproxy.local;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import org.zhuboss.pc2server.pcproxy.ProxyAddrResolve;

public class Client4LocalChannelInitializer extends ChannelInitializer<SocketChannel>{

	private boolean isHttps =true;
	
	public Client4LocalChannelInitializer(){
		zhuboss.pc2server.common.ProxyAddrResolveUtil.Result result = ProxyAddrResolve.resolve();
		isHttps = result.isHttps();
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if(isHttps == true){
			ch.pipeline().addLast("ssl", this.createSslHandler(ch)); //客户端，接收Request对象
	    	
	    	/**
	    	 * down
	    	 */
	        ch.pipeline().addLast("read_handler",new Client4LocalHandler());
	        
	        /**
	         * up
	         */
	        ch.pipeline().addLast("HttpRequestDecoder", new HttpRequestDecoder());
		}else{
			ch.pipeline().addLast("read_handler",new Client4LocalHandler());
		}
		
	}

	private SslHandler createSslHandler(SocketChannel ch) throws Exception{
		
		SSLEngine engine = SslContext.newClientContext().newEngine(ch.alloc());
        engine.setUseClientMode(true);
        engine.setNeedClientAuth(true);  
        
		return new SslHandler(engine);
	}

}
