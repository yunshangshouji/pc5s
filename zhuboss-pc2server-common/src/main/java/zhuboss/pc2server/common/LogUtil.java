package zhuboss.pc2server.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class LogUtil {
	
	static Logger logger = LoggerFactory.getLogger(LogUtil.class);
	
	public enum ROLE{
		SERVER4WWW,
		Server4Client,
		Client4Server,
		Client4Local
	}
	
	public enum Event{
		channelActive,
		channelInactive,
		channelRead,
		channelReadComplete,
		exceptionCaught,
		channelRegistered,
		channelUnregistered
	}
	
	public enum Do{
		READ,
		WRITE,
		CONNECT,
		DISCONNECT,
		EXCEPTION_CAUGHT
	}
	
	public static String log(
			Channel channel ,
			Event event){
		return "【"+LogUtil.channelHandlerContext2String(channel,null)+"】【"+event+"】";
	}
	
	public static String log(
						Do d, 
						Channel channel,
						String data){
		return "【"+LogUtil.channelHandlerContext2String(channel,d)+"】【"+d+"】【"  +data+"】";
	}
	
	public static String channelHandlerContext2String(Channel channel,Do d){
		String oper = null;
		if( d == null){
			oper = "";
		}else if( d.equals(Do.READ)){
			oper = "<<";
		}else if( d.equals(Do.WRITE)){
			oper = ">>";
		}else if( d.equals(Do.CONNECT)){
			oper = "><";
		}else if( d.equals(Do.DISCONNECT)){
			oper = "<>";
		}
		
		return channel.localAddress() + oper + channel.remoteAddress();
	}
}
