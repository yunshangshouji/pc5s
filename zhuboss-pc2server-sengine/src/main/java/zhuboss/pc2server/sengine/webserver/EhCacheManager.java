package zhuboss.pc2server.sengine.webserver;

import java.io.Serializable;
import java.util.concurrent.Callable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;

public class EhCacheManager implements InitializingBean , DisposableBean{
	Logger logger = LoggerFactory.getLogger(EhCacheManager.class);
	
	public enum CACHE{
		/**
		 * 用户流量上限
		 */
		FLOW_LIMIT,
		/**
		 * 访问权限配置
		 */
		ACCESS_CFG,
		/**
		 * domain转userId
		 */
		DOMAIN_USERID,
		/**
		 * 缓存配置
		 */
		USER_DOMAIN_CACHE
		
	}
	
	CacheManager manager; 

	public Cache getCache( CACHE cache){
		return manager.getCache(cache.name());
	}
	
	
	public <T> T getData(CACHE cacheName, final Serializable key,Callable<T> call){
		Cache cache = this.getCache(cacheName);
		T data ;
		synchronized(key){
			Element  element = cache.get(key);
			if(element == null){
				try {
					data = call.call();
				} catch (Exception e) {
					data = null;
					logger.error(e.getMessage(),e);
				}
				element = new Element(key, data);
				cache.put(element);
			}else{
				data = (T)element.getObjectValue();
			}
		}
		return data;
	}
	
	@Override
	public void destroy() throws Exception {
		manager.shutdown();
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		manager = CacheManager.newInstance(new ClassPathResource("ehcache.xml").getInputStream()); 
		
	}
}
