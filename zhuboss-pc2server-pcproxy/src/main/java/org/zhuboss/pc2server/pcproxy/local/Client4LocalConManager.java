package org.zhuboss.pc2server.pcproxy.local;

import io.netty.channel.ChannelFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import zhuboss.pc2server.common.JavaUtil;

public class Client4LocalConManager {
	static Client4LocalConManager client4LocalConManager = new Client4LocalConManager();
	public static Client4LocalConManager getInstance(){
		return client4LocalConManager;
	}

	Map<UUID,ChannelFuture> map = new HashMap<UUID,ChannelFuture>();
	Map<ChannelFuture,UUID> map2 = new HashMap<ChannelFuture,UUID>();
	
	/**
	 * 未连接则建立 连接，连接关闭状态也重新创建连接
	 * @param uuidBytes
	 * @return
	 * @throws InterruptedException
	 */
	public synchronized ChannelFuture getSafeChannelFuture(byte[] uuidBytes,byte zipFlag) throws InterruptedException {
		UUID uuid = JavaUtil.bytes2UUID(uuidBytes);
		ChannelFuture ctx = map.get(uuid);
		if(ctx==null || ! ctx.channel().isOpen()){ //为空或已经被客户端关闭
			ctx = Client4Local.getInstance().build(uuidBytes,zipFlag);
			map.put(uuid, ctx);
			map2.put(ctx,uuid);
		}
		return ctx;
	}
	
	public synchronized void unRegister(UUID uuid){
		ChannelFuture channelFuture = map.get(uuid);
		if(channelFuture!=null){
			if(channelFuture.channel()!=null){
				channelFuture.channel().close();
			}
			map2.remove(channelFuture);
		}
		map.remove(uuid);
		
	}
}
