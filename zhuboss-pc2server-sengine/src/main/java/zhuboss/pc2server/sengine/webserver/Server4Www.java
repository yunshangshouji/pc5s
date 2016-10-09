package zhuboss.pc2server.sengine.webserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import zhuboss.pc2server.sengine.LogChannelHandlerAdapter;
import zhuboss.pc2server.sengine.webserver.bytebuf2response.BytebufToHttpResponseEncoder;
import zhuboss.pc2server.sengine.webserver.bytebuf2response.MyHttpContentCompressor;

/**
 * 参考：http://www.zicheng.net/article/84.htm
 * @author Administrator
 *
 */
public class Server4Www implements InitializingBean,DisposableBean {
	Logger logger = LoggerFactory.getLogger(this.getClass());

//	@Value("#{propertyConfigurer.getContextProperty('sengine.webserver.port1')}")
//	String port1;
	@Value("#{propertyConfigurer.getContextProperty('sengine.webserver.port')}")
	String port2;
	@Value("#{propertyConfigurer.getContextProperty('sengine.domain')}")
	String sengineDomain;
	@Value("#{propertyConfigurer.getContextProperty('sengine.isbackend')}")
	String sengineIsbackend;
	EventLoopGroup connectionManagerGroup = new NioEventLoopGroup();//接收进来的连接
	EventLoopGroup workerGroup = new NioEventLoopGroup(); //处理已经被接收的连接
	@Override
	public void afterPropertiesSet() throws Exception {

		 /**
		  * ServerBootstrap 是一个启动NIO服务的辅助启动类
		  */
		 ServerBootstrap b = new ServerBootstrap();
		 b = b.group(connectionManagerGroup, workerGroup).channel(NioServerSocketChannel.class);
		 b = b.childOption(ChannelOption.SO_KEEPALIVE, false);
		 b = b.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LogChannelHandlerAdapter("Server4Www"));
				
				
				//upstream up-down RequestDecoder->RequestAggregator->deflater->RequestBusinessHandler
				ch.pipeline().addLast("RequestDecoder", new HttpRequestDecoder());
				ch.pipeline().addLast("RequestAggregator",new HttpObjectAggregator(10*1024*1024));//max 10m，注意：不合并httpobject在传输层会引起混乱,因为Client4Server的encoder绑定了channel
				
				
				
				
				//downstream down-up BytebufToHttpResponseEncoder->deflater->ResponseEncoder
				//TODO HTTP 1.0 gzip 是跳过了，但HttpContent不是LastHttpContent，客户端还在等待。。
				ch.pipeline().addLast("ResponseEncoder",new HttpResponseEncoder());//HttpResponse(Message、Content) -> ByteBuf

//前端有nginx,使用nginx压缩
if(sengineIsbackend==null || sengineIsbackend.equals("false")){
	ch.pipeline().addLast("deflater", new MyHttpContentCompressor()); //MSG condition,Prepare HttpRequest,After HttpResponse
}
				
				ch.pipeline().addLast("BytebufToHttpResponseEncoder", new BytebufToHttpResponseEncoder(sengineDomain));//ByteBuf->HttpResponse(Message、Content) & header business logic
				
				ch.pipeline().addLast("RequestBusinessHandler",new Server4WwwHandler());
				ch.pipeline().addLast(new LogChannelHandlerAdapter("Server4Www"));
			}
			 
		 });

		 b = b.option(ChannelOption.SO_BACKLOG, 128);
		 b = b.childOption(ChannelOption.SO_KEEPALIVE, true);
//		 ChannelFuture f = b.bind(Integer.parseInt(port1)).sync();
		 ChannelFuture f = b.bind(Integer.parseInt(port2)).sync();
		 logger.info("监听端口：" + port2);
	 
	}
	@Override
	public void destroy() throws Exception {
		logger.info("Server4Www destroy...");
		workerGroup.shutdownGracefully();
		connectionManagerGroup.shutdownGracefully();
		
	}
	 
}
