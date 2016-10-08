package org.zhuboss.pc2server.pcproxy.server;

import io.netty.channel.Channel;

/**
 * web server与client server的绑定关系
 * @author Administrator
 *
 */
public class Client4ServerRegister {
	private static Client4ServerRegister instance =  new Client4ServerRegister();
	public static Client4ServerRegister getInstance(){
		return instance;
	}
	Channel channel;
	//多用户，Key为用户Id
//	Map<MyAddress,ChannelHandlerContext> map = new HashMap<MyAddress,ChannelHandlerContext>();
	
	public synchronized void register(Channel channel){
		this.channel = channel;
//		InetSocketAddress address = (InetSocketAddress)clientChannelHandlerContext.channel().remoteAddress();
//		map.put(NetUtil.convet(address),clientChannelHandlerContext);
	}
	public synchronized void unRegister(Channel channel){
		channel = null;
	}
	
	public Channel getChannel(){
		return channel;
	}
	
}
