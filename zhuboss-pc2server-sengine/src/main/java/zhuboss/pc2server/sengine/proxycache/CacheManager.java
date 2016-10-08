package zhuboss.pc2server.sengine.proxycache;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.util.CryptoUtils;
import zhuboss.pc2server.sengine.Constants;
import zhuboss.pc2server.sengine.mis.mapper.UserDomainCachePOMapper;
import zhuboss.pc2server.sengine.mis.mapper.UserDomainPOMapper;
import zhuboss.pc2server.sengine.mis.po.UserDomainCachePO;
import zhuboss.pc2server.sengine.mis.po.UserDomainPO;
import zhuboss.pc2server.sengine.webserver.EhCacheManager;
import zhuboss.pc2server.sengine.webserver.EhCacheManager.CACHE;

public class CacheManager implements InitializingBean {
	Logger logger = LoggerFactory.getLogger(CacheManager.class);
	
	@Autowired
	EhCacheManager ehCacheManager;
	@Autowired
	UserDomainCachePOMapper userDomainCachePOMapper;
	@Autowired
	UserDomainPOMapper userDomainPOMapper;
	
	@Value("#{propertyConfigurer.getContextProperty('sengine.cache.dir')}")
	String CACHE_DIR ;
	Map<String,LURCache> cacheMap = new HashMap<String,LURCache>(); //domain,cache
	final static int BUFFER_SIZE = 4096;  
	
	Timer timerIndex;
	Timer timerFile;
	@Override
	public void afterPropertiesSet() throws Exception {
		timerIndex = new Timer("cache_clean_index", true);
		timerIndex.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Timer execute purge index ...");
				try{
					Iterator<String> iterator = cacheMap.keySet().iterator();
					while(iterator.hasNext()){
						String key = iterator.next();
						LURCache lurCache = cacheMap.get(key);
						lurCache.purgeIndex();
						
					}
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}, TimeUnit.SECONDS.toMillis(30) //30秒后执行
		,TimeUnit.MINUTES.toMillis(1) ); //1 minutes
		
		//
		timerFile = new Timer("cache_clean_file", true);
		timerFile.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Timer execute purge file...");
				try{
					Iterator<String> iterator = cacheMap.keySet().iterator();
					while(iterator.hasNext()){
						String key = iterator.next();
						LURCache lurCache = cacheMap.get(key);
						lurCache.purgeFiles();
						
					}
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}, TimeUnit.MINUTES.toMillis(1) //30秒后执行
		,TimeUnit.DAYS.toMillis(1) ); //1 minutes
	}
	
	public void loadFile(String userDomain,String key,ByteBuf  content) throws IOException{
		FileInputStream fis = new FileInputStream(new File(CACHE_DIR+"/"+userDomain+"/"+key));
        int count = -1;  
        while((count = content.writeBytes(fis, BUFFER_SIZE)) != -1)  ;  
	}
	
	/**
	 * 是否具有缓存策略，有则返回fileId
	 * @param userDomain
	 * @param uri
	 * @param args
	 * @return
	 */
	public String getCacheKey(String userDomain,String uri,String args){
		/**
		 * 是否满足缓存策略
		 */
		List<UserDomainCachePO>  userDomainCachePOList = this.getUserDomainCache(userDomain);
		UserDomainCachePO userDomainCachePO = this.findRule(userDomainCachePOList, uri);
		if(userDomainCachePO != null){
			String rawStr;
			if(userDomainCachePO.getCaseArgs()!=null && userDomainCachePO.getCaseArgs().equals("1")){
				rawStr = userDomain + uri + args;
			}else{
				rawStr = userDomain + uri ;
			}
			String key = CryptoUtils.md5Digest(rawStr);
			return key;
		}
		return null;
	}
	
	/**
	 * 
	 * @param userDomain
	 * @return getLURCache非法则返回null
	 */
	public LURCache getLURCache(String userDomain){
		LURCache lurCache = cacheMap.get(userDomain);
		if(lurCache==null){
			/**
			 * 从文件加载
			 */
			File dir = new File(CACHE_DIR+"/"+userDomain);
			if(!dir.exists()){
				dir.mkdir();
			}
			File index = new File(CACHE_DIR+"/"+userDomain+"/index");
			if(index.exists()){
				ObjectInputStream ois = null;
				try{
					ois = new ObjectInputStream(new FileInputStream(index)); 
					lurCache = (LURCache)ois.readObject();
				}catch(Exception e){
					logger.warn(e.getMessage(),e);
				}finally{
					if(ois!=null){
						try {
							ois.close();
						} catch (IOException e) {
							logger.warn(e.getMessage());
						}
					}
				}
			}
			/**
			 * 新建
			 */
			if (lurCache == null){
				//TODO 从配置文件读取配置
				lurCache = new LURCache(CACHE_DIR+"/"+userDomain+"/",100, 10*1024*1024);
			}
			cacheMap.put(userDomain, lurCache);
		}
		return lurCache;
	}
	
	public LRUElement getCacheElement(String userDomain,String key){
		LURCache lurCache = this.getLURCache(userDomain);
		LRUElement element = lurCache.get(key);
		return element;
	}
	
	public void addCache(String userDomain,String key,String uri,long lastModified,byte[] data){
		List<UserDomainCachePO>  userDomainCachePOList = this.getUserDomainCache(userDomain);
		UserDomainCachePO userDomainCachePO = this.findRule(userDomainCachePOList, uri);
		//
		LURCache lurCache = this.getLURCache(userDomain);
		LRUElement element = lurCache.get(key);
		if(element != null){
			logger.warn("cache exists , should not addCache again");
			return; //已存在，暂时不更新
		}
		//
		long milliSeconds = TimeUnit.valueOf(userDomainCachePO.getExpireUnit()).toMillis(userDomainCachePO.getExpireTime());
		try {
			lurCache.add(key, uri, new Date().getTime() + milliSeconds, lastModified, data);
		} catch (FileTooBigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private UserDomainCachePO findRule(List<UserDomainCachePO> userDomainCachePOList,String uri){
		for(UserDomainCachePO userDomainCachePO : userDomainCachePOList){
			boolean caseSensitive = userDomainCachePO.getCaseSensitive().equals("1");
			if(userDomainCachePO.getHowMatch().equals(Constants.HOW_MATCH.EQUALS.name())){
				for(String  l : userDomainCachePO.getLocationList()){
					if(caseSensitive? uri.equals(l):uri.toLowerCase().equals(l.toLowerCase())){
						return userDomainCachePO;
					}
				}
			}else if (userDomainCachePO.getHowMatch().equals(Constants.HOW_MATCH.STARTS.name())){
				for(String  l : userDomainCachePO.getLocationList()){
					if(caseSensitive? uri.startsWith(l):uri.toLowerCase().startsWith(l.toLowerCase())){
						return userDomainCachePO;
					}
				}
			}else if (userDomainCachePO.getHowMatch().equals(Constants.HOW_MATCH.ENDS.name())){
				for(String  l : userDomainCachePO.getLocationList()){
					if(caseSensitive? uri.endsWith(l):uri.toLowerCase().endsWith(l.toLowerCase())){
						return userDomainCachePO;
					}
				}
			}else if (userDomainCachePO.getHowMatch().equals(Constants.HOW_MATCH.CONTAINS.name())){
				for(String  l : userDomainCachePO.getLocationList()){
					if(caseSensitive? uri.contains(l):uri.toLowerCase().contains(l.toLowerCase())){
						return userDomainCachePO;
					}
				}
			}
		}
		return null;
	}

	private List<UserDomainCachePO> getUserDomainCache(final String userDomain){
		return ehCacheManager.getData(CACHE.USER_DOMAIN_CACHE, userDomain, new Callable<List<UserDomainCachePO>>() {
			@Override
			public List<UserDomainCachePO> call() throws Exception {
				List<UserDomainPO> userDomainPOList = userDomainPOMapper.selectByClause(new QueryClauseBuilder().andEqual("user_domain", userDomain));
				if(userDomainPOList.size()!=1 ){
					throw new RuntimeException("user domain not exists["+userDomain+"]");
				}
				List<UserDomainCachePO> list = userDomainCachePOMapper.selectByClause(new QueryClauseBuilder().andEqual("user_domain_id", userDomainPOList.get(0).getId()));
				Collections.sort(list, new Comparator<UserDomainCachePO>() {
					@Override
					public int compare(UserDomainCachePO o1,
							UserDomainCachePO o2) {
						return getIntVal(o1.getHowMatch()) - getIntVal(o2.getHowMatch());
					}
				});
				return list;
			}
		});
	}
	
	
	private int getIntVal(String howMatch){
		return howMatch.equals("equals")?1:(howMatch.equals("starts")?2:3);
	}
	
	/* ------------------------------------------------------------ */
    /* Refrence org.eclipse.jetty.servlet.DefaultServlet
     */
    public boolean isNotModified(String userDomain, String key, HttpHeaders headers)
    {
    	LURCache lurCache = cacheMap.get(userDomain);
    	LRUElement lruElement = lurCache.get(key);
    	long resourceLastModified = lruElement.getLastModified();
    	
    	/* 暂不支持etag
        
        */
        // Handle if modified since
        String ifms=  getField(headers, HttpHeader.IF_MODIFIED_SINCE.asString());
        if (ifms!=null)
        {
            //Get jetty's Response impl
            String mdlm= getField(headers,HttpHeader.LAST_MODIFIED.asString());
            if (mdlm!=null && ifms.equals(mdlm))
            {
               return true;
            }

            long ifmsl= this.getDateField(getField(headers,HttpHeader.IF_MODIFIED_SINCE.asString()));
            if (ifmsl!=-1 && resourceLastModified/1000 <= ifmsl/1000)
            { 
               return true;
            }
        }

        // Parse the if[un]modified dates and compare to resource
        long date= getDateField(getField(headers,HttpHeader.IF_UNMODIFIED_SINCE.asString())) ;
        if (date!=-1 && resourceLastModified/1000 > date/1000)
        {
            return false;
        }
            
        return false;
    }
    private String getField(HttpHeaders headers, String name){
    	if(headers.get(name)==null){
    		return null;
    	}
    	return headers.get(name).toString();
    }
    
    public static long getDateField(String field)
    {
    	if(field == null)
            return -1;

        String val = valueParameters(field, null);
        if (val == null)
            return -1;

        final long date = DateParser.parseDate(val);
        if (date==-1)
            throw new IllegalArgumentException("Cannot convert date: " + val);
        return date;
    }
    public static String valueParameters(String value, Map<String,String> parameters)
    {
        if (value == null) return null;

        int i = value.indexOf(';');
        if (i < 0) return value;
        if (parameters == null) return value.substring(0, i).trim();

        StringTokenizer tok1 = new QuotedStringTokenizer(value.substring(i), ";", false, true);
        while (tok1.hasMoreTokens())
        {
            String token = tok1.nextToken();
            StringTokenizer tok2 = new QuotedStringTokenizer(token, "= ");
            if (tok2.hasMoreTokens())
            {
                String paramName = tok2.nextToken();
                String paramVal = null;
                if (tok2.hasMoreTokens()) paramVal = tok2.nextToken();
                parameters.put(paramName, paramVal);
            }
        }

        return value.substring(0, i).trim();
    }

	
}
