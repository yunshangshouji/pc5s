package zhuboss.pc2server.sengine.webserver;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zhuboss.pc2server.common.JavaUtil;

public class Server4WwwConManager {
	private static Logger logger = LoggerFactory.getLogger(Server4WwwConManager.class);
	private static Server4WwwConManager instance =  new Server4WwwConManager();
	
	public static Server4WwwConManager getInstance(){
		return instance;
	}
	
	Map<ChannelHandlerContext,UUID> mapA = new HashMap<ChannelHandlerContext,UUID>();
	Map<UUID,ChannelHandlerContext> mapR = new HashMap<UUID,ChannelHandlerContext>();
	
	public synchronized void register(ChannelHandlerContext ctx){
		UUID uuid = UUID.randomUUID();
		logger.debug("register:"+uuid);
		mapA.put(ctx, uuid);
		mapR.put(uuid, ctx);
	}
	
	public synchronized void unRegister(ChannelHandlerContext ctx){
		UUID uuid = mapA.get(ctx);
		logger.info("unRegister:"+uuid+","+ctx.channel().remoteAddress());
		mapA.remove(ctx);
		mapR.remove(uuid);
	}
	
	public synchronized ChannelHandlerContext getChannelHandlerContext(byte[] uuidBytes){
		return mapR.get(JavaUtil.bytes2UUID(uuidBytes));
	}
	
	public synchronized UUID getUUID(ChannelHandlerContext ctx){
		return mapA.get(ctx);
	}
}
