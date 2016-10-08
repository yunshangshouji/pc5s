package org.zhuboss.pc2server.pcproxy.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhuboss.pc2server.pcproxy.util.PropertiesUtil;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.bufparser.TransPackage;
import zhuboss.pc2server.common.bufparser.TransPackageDecoder;
import zhuboss.pc2server.common.bufparser.TransPackageEncoder;

public class Client4Server {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	Bootstrap b;
	EventLoopGroup workerGroup;
	Thread thread ;
	
	public void start (final ConnectedListener connectedListener) throws Exception{
		//
		 workerGroup = new NioEventLoopGroup();
	        try {
	            b = new Bootstrap();
	            b.group(workerGroup);
	            b.channel(NioSocketChannel.class);
	            b.option(ChannelOption.SO_KEEPALIVE, true);
	            b.handler(new ChannelInitializer<SocketChannel>() {
	                @Override
	                public void initChannel(SocketChannel ch) throws Exception {
	                	 ch.pipeline().addLast("Decoder",new TransPackageDecoder(false));
	                	 ch.pipeline().addLast("BusinessLogic",new Client4ServerHandler(connectedListener));
	                	 
		                    ch.pipeline().addLast("Encoder",new TransPackageEncoder());
		                    
//	                	//downstream
//	                	ch.pipeline().addLast("Encoder",new TransPackageEncoder());
//	                	//upstream
//	                    ch.pipeline().addLast("Decoder",new TransPackageDecoder());
//	                    ch.pipeline().addLast("BusinessLogic",new Client4ServerHandler());
	                    
	                }
	            });

	            // Start the client.
	            
	            
	            
	          
	        }finally{
	        	
	        }
	        
	        thread = new Thread(new AutoConnectThreadRun(this));
	        thread.start();
	}
	
	
	public void connect() {
		String reverseLocal = "pc5s.com:82";
		String cfgReverseLocal = PropertiesUtil.getPropertyString("pcproxy.reverse.remote");
		if(cfgReverseLocal!=null && !cfgReverseLocal.equals("")){
			reverseLocal = cfgReverseLocal;
		}
		String host = reverseLocal.split("\\:")[0];
		int port = Integer.parseInt(reverseLocal.split("\\:")[1]);
		
		ChannelFuture f ;
		while(true){
			try{
				logger.info("Connecting server ..."); //+reverseLocal
				f = b.connect(host, port).sync();
				break;
			}catch( Exception e){
				logger.warn(e.getMessage());
			}
		}
		/**
		 * 校验用户名密码
		 */
		byte[] data = (org.zhuboss.pc2server.pcproxy.Constants.VERSION + "," + System.getProperty(org.zhuboss.pc2server.pcproxy.Constants.AUTH_KEY)+","+System.getProperty(org.zhuboss.pc2server.pcproxy.Constants.PROXY_ADDR)).getBytes();
		TransPackage transPackage = new TransPackage(null, Constants.OPERATE_TYPE_AUTH, Constants.ZIP_FLAG_NO,data);
		f.channel().writeAndFlush(transPackage);
	}
	
	public void stop () throws Exception{
		try{
			if(thread!=null && thread.isAlive()){
				thread.stop();
			}
			workerGroup.shutdownGracefully().sync();
			workerGroup = null;
		}catch(Throwable t){
			logger.error(t.getMessage(),t);
		}
	}
}
