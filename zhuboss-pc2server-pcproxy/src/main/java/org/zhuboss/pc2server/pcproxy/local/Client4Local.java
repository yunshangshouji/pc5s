package org.zhuboss.pc2server.pcproxy.local;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import org.zhuboss.pc2server.pcproxy.ProxyAddrResolve;

public class Client4Local {
	private static Client4Local instance ;
	public static Client4Local getInstance(){
		if(instance == null){
			try {
				instance = new Client4Local();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	public Client4Local() throws Exception{
		this.connect();
	}
	Bootstrap b;
	public void connect () throws Exception{
		 EventLoopGroup workerGroup = new NioEventLoopGroup();

	        try {
	            b = new Bootstrap();
	            b.group(workerGroup);
	            b.channel(NioSocketChannel.class);
	            b.option(ChannelOption.SO_KEEPALIVE, true);
	            b.option(ChannelOption.TCP_NODELAY, true);
	            //每次read事件buffer不超1024，保证写不超1024+16
	            b = b.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(10240, 10240, 102400)); //增大 后，可以增大TransPackage大小，提高gzip压缩率
	            b.handler(new Client4LocalChannelInitializer());

	            // Start the client.
	            
	            
	            
	          
	        }finally{
	        	
	        }
	}
	public ChannelFuture build(byte[] uuidBytes,byte zipFlag) throws InterruptedException{
		zhuboss.pc2server.common.ProxyAddrResolveUtil.Result result = ProxyAddrResolve.resolve();
		ChannelFuture f = b.connect(result.getHost(), result.getPort()).sync(); 
		Client4LocalHandler client2LocalHandler = (Client4LocalHandler) f.channel().pipeline().get("read_handler");
		client2LocalHandler.setUuidBytes(uuidBytes);
		client2LocalHandler.setZipFlag(zipFlag);
		return f;
		
	}
	
	private SslHandler createSslHandler(SocketChannel ch) throws Exception{
		
		SSLEngine engine = SslContext.newClientContext().newEngine(ch.alloc());
        engine.setUseClientMode(true);
        engine.setNeedClientAuth(true);  
        
		return new SslHandler(engine);
	}

	public static void main(String[] args) throws Exception {
		Client4Local client = new Client4Local();
		/**
		 * 注意HTTP协议格式,0结尾
		 */
        Thread.sleep(20*1000);
        
    }
	
}
