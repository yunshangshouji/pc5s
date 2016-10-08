package zhuboss.pc2server.sengine.clientserver;

import io.netty.channel.Channel;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import zhuboss.pc2server.common.Constants;
import zhuboss.pc2server.common.bufparser.TransPackage;

/**
 * web server与client server的绑定关系
 * @author Administrator
 *
 */
public class Server4ClientRegister {
	private static Server4ClientRegister instance =  new Server4ClientRegister();
	public static Server4ClientRegister getInstance(){
		return instance;
	}
	
	
	/**
	 * key:userDomain
	 * 注意：value是单实例的，如果有第二个连接连上去，则会冲掉第一个连接
	 */
	ConcurrentHashMap<String,ChannelInfo> map = new ConcurrentHashMap<String,ChannelInfo>();
	
	public Map<String,ChannelInfo> getMap(){
		return map;
	}
	
	public ChannelInfo getChannelInfo(String userDomain){
		ChannelInfo channelInfo =  map.get(userDomain);
		if(channelInfo!=null && channelInfo.getChannel().isActive()){
			return channelInfo;
		}
		return null;
	}
	
	public  void register(String userDomain, ChannelInfo channelInfo){
		/**
		 * 是否已有注册
		 */
		ChannelInfo old = map.get(userDomain);
		if(old!=null && old.getChannel().isActive()){
				TransPackage ret = new TransPackage(null,Constants.OPERATE_TYPE_AUTH,Constants.ZIP_FLAG_NO,(" 其它地方登录["+channelInfo.getChannel().remoteAddress()+"],本地退出").getBytes(Charset.forName("UTF-8")));
				old.getChannel().writeAndFlush(ret);
		}
		//
		map.put(userDomain,channelInfo);
	}
	public  void unRegister(Channel channel){
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String item = iterator.next();
			if(map.get(item).getChannel().equals(channel)){
				iterator.remove();
				return;
			}
		}
	}
}
