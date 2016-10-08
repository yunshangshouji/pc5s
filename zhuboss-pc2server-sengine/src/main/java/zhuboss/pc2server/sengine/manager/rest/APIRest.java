package zhuboss.pc2server.sengine.manager.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import zhuboss.pc2server.common.CacheResource;
import zhuboss.pc2server.sengine.StartSEngine;
import zhuboss.pc2server.sengine.clientserver.ChannelInfo;
import zhuboss.pc2server.sengine.clientserver.Server4ClientRegister;
import zhuboss.pc2server.sengine.clientserver.TranficStatis;
import zhuboss.pc2server.sengine.proxycache.CacheManager;
import zhuboss.pc2server.sengine.proxycache.LRUElement;
import zhuboss.pc2server.sengine.proxycache.LURCache;


@Path("/")
public class APIRest {
	
	@GET
	@Path("clients/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String[]> clients(){
		List<String[]> list = new ArrayList<String[]>();
		Iterator<String> iterator = Server4ClientRegister.getInstance().getMap().keySet().iterator();
		while(iterator.hasNext()){
			String domain = iterator.next();
			ChannelInfo channelInfo = Server4ClientRegister.getInstance().getMap().get(domain);
			list.add(new String[]{
					domain,channelInfo.getChannel().remoteAddress().toString(),channelInfo.getHost()
			});
		}
		return list;
	}
	
	@GET
	@Path("clients/count")
	public Integer clientsCount(){
		return Server4ClientRegister.getInstance().getMap().size();
	}
	
	@GET
	@Path("tranfic/list")
	@Produces(MediaType.APPLICATION_JSON)
	public Object tranficList(){
		return TranficStatis.getInstance().getMap();
	}
	
	@GET
	@Path("cache/list/{userDomain}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CacheResource> getCacheList(@PathParam("userDomain")String userDomain){
		CacheManager cacheManager = StartSEngine.getApplicationContext().getBean(CacheManager.class);
		LURCache lurCache = cacheManager.getLURCache(userDomain);
		Iterator<Entry<String, LRUElement>> iterator = lurCache.entrySet().iterator();
		List<CacheResource>  list = new ArrayList<CacheResource>();
		while(iterator.hasNext()){
			Entry<String, LRUElement> entry = iterator.next();
			list.add(new CacheResource(entry.getValue().getRequestURI(),
					entry.getValue().getBytes(),
					new Date(entry.getValue().getExpireDate())
					));
		}
		return list;
	}
	
	@GET
	@Path("cache/clear/{userDomain}")
	@Produces(MediaType.APPLICATION_JSON)
	public void clearCache(@PathParam("userDomain")String userDomain){
		CacheManager cacheManager = StartSEngine.getApplicationContext().getBean(CacheManager.class);
		LURCache lurCache = cacheManager.getLURCache(userDomain);
		lurCache.removeAll();
	}
}
