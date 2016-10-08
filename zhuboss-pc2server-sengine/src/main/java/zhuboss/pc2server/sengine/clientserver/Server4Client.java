package zhuboss.pc2server.sengine.clientserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import zhuboss.pc2server.common.bufparser.TransPackageDecoder;
import zhuboss.pc2server.common.bufparser.TransPackageEncoder;
import zhuboss.pc2server.sengine.LogChannelHandlerAdapter;
import zhuboss.pc2server.sengine.clientserver.encode.MyHttpRequestEncoder;
import zhuboss.pc2server.sengine.clientserver.encode.MyHttpRequestEncoder2TransPackage;
/**
 * 参考：http://www.zicheng.net/article/84.htm
 * @author Administrator
 *
 */
public class Server4Client implements InitializingBean,DisposableBean {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("#{propertyConfigurer.getContextProperty('sengine.clientserver.port')}")
	int port;
	EventLoopGroup connectionManagerGroup = new NioEventLoopGroup();//接收进来的连接
	EventLoopGroup workerGroup = new NioEventLoopGroup(); //处理已经被接收的连接

	@Override
	public void afterPropertiesSet() throws Exception {
			 /**
			  * ServerBootstrap 是一个启动NIO服务的辅助启动类
			  */
			 ServerBootstrap b = new ServerBootstrap();
			 b = b.group(connectionManagerGroup, workerGroup);
			 //ServerSocketChannel以NIO的selector为基础进行实现的，用来接收新的连接
			 b = b.channel(NioServerSocketChannel.class);
//			 b.childOption(ChannelOption.SO_RCVBUF, 1024+17);
			 
			 b = b.childHandler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LogChannelHandlerAdapter("Server4Client"));
					
					 //upstream<read> 从上到下
					 ch.pipeline().addLast("DecoderTransPackage",new TransPackageDecoder(true)); //1. ByteBuf->TransPackage、ByteBuf(HttpResponse)
					 ch.pipeline().addLast("BusinessLogic",new Server4ClientHandler()); //2. dispatch TransPackage & write ByteBuf to server4Www channel
					 
					 //downstream<write> 从下向上
					ch.pipeline().addLast("Encoder",new TransPackageEncoder()); //3.TransPackage-->Bytebuf(底层数据)
					ch.pipeline().addLast("Encoder_http_buf",new MyHttpRequestEncoder2TransPackage());//2. Bytebuf->TransPackage(传输数据)
					ch.pipeline().addLast("Encoder_http_request",new MyHttpRequestEncoder()); //1. request->Bytebuf(应用数据)
					
					ch.pipeline().addLast(new LogChannelHandlerAdapter("Server4Client"));
				}
				 
			 });
	
			 b = b.option(ChannelOption.SO_BACKLOG, 128);
			 b = b.childOption(ChannelOption.SO_KEEPALIVE, true);
	
			 ChannelFuture f = b.bind(port).sync();
			 logger.info("监听端口：" + (port));
		
			 
	}
	@Override
	public void destroy() throws Exception {
		logger.info("Server4Client destroy...");
		workerGroup.shutdownGracefully();
		 connectionManagerGroup.shutdownGracefully();
		
	}

	
}