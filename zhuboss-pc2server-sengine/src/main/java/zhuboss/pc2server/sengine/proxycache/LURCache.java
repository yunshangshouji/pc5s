package zhuboss.pc2server.sengine.proxycache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.最大元素数量<br>
 * 2.元素相加最大字节数<br>
 * 3.元素的过期时间
 *
 * @param <K>
 * @param <V>
 */
public class LURCache extends LinkedHashMap<String,LRUElement>{

	private static final long serialVersionUID = 1766248530203544257L;
	Logger logger = LoggerFactory.getLogger(CacheManager.class);
	
	private final int MAX_ENTRIES;
	private final int MAX_BYTES;
	private int totalBytes = 0;
	private String DATA_PATH;
	
	public LURCache(String dataPath,int maxEntries,int maxBytes) {
		 super((int) Math.ceil(maxEntries / 0.75) + 1, 0.75f, true);//true for access-order, false for insertion-order
		 this.DATA_PATH = dataPath;
		 this.MAX_ENTRIES = maxEntries;
		 this.MAX_BYTES = maxBytes;
		 /**
		  * check dir exists
		  */
		 File dir = new File(DATA_PATH);
		if(!dir.exists()){
			dir.mkdir();
		}
	}


	public synchronized LRUElement add(String key, String requestURI, long expireDate, long lastModified,byte[] data) throws FileTooBigException{
		LRUElement value = new LRUElement(requestURI, expireDate, lastModified, data.length);
		//数量控制
		if(size() == MAX_ENTRIES ){
			Iterator<String> iterator = this.keySet().iterator();
			String removeKey = iterator.next();
			this.remove(removeKey);
			this.syncIndex();
			removeFile(removeKey);
		}
		//容量控制
		if(value.getBytes() > MAX_BYTES){
			throw new FileTooBigException("");
		}
		if(totalBytes + value.getBytes() > MAX_BYTES){
			//释放
			Iterator<Map.Entry<String,LRUElement>> iterator = this.entrySet().iterator();
			while(iterator.hasNext() && (totalBytes + value.getBytes() > MAX_BYTES) ){
				Map.Entry<String,LRUElement> entry = iterator.next();
				iterator.remove();
				this.syncIndex();
				removeFile(entry.getKey());
				totalBytes -= entry.getValue().getBytes();
			}
		}
		LRUElement  lruElement = super.put(key, value);
		try {
			this.writeCacheFile(key,data);
			this.syncIndex();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lruElement;
	}

	public synchronized void removeAll(){
		this.clear();
		this.totalBytes = 0;
		File dir = new File(this.DATA_PATH);
		File[] files = dir.listFiles();
		for(File file : files){
			if(!file.getName().equals("index")){
				file.delete();
			}
		}
		
	}
	private  synchronized void writeCacheFile(String key,byte[] bytes) throws Exception{
		OutputStream os = null;
		try{
			File file = new File(DATA_PATH+key);
			os = new FileOutputStream(file);
			os.write(bytes);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(os!=null){
				try {
					os.flush();
					os.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	
	private void syncIndex(){
		File index = new File(DATA_PATH+"index");
		try {
			FileOutputStream fos = new FileOutputStream(index);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();
			fos.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		
	}
	
	
	private void removeFile(String key){
		File file = new File(DATA_PATH+key);
		if(file.exists()){
			file.delete();
		}
	}
	
	public LRUElement getElement(String key){
		return super.get(key);
	}


	public synchronized void purgeIndex(){
		long now = new Date().getTime();
		Iterator<Map.Entry<String,LRUElement>> iterator = this.entrySet().iterator();
		int del=0;
		while(iterator.hasNext() ){
			Map.Entry<String,LRUElement> entry = iterator.next();
			//缓存过期
			if(!new File(this.DATA_PATH+entry.getKey()).exists()){ //文件不存在
				iterator.remove();
				del++;
			}else if(entry.getValue().getExpireDate() < now ){
				iterator.remove();
				del++;
				this.removeFile(entry.getKey());
			}
		}
		if(del>0){
			countTotalBytes();
		}
	}
	
	public synchronized void purgeFiles(){
		File dir = new File(this.DATA_PATH);
		File[] files = dir.listFiles();
		for(File file : files){
			if(!file.getName().equals("index") && this.get(file.getName()) == null){
				file.delete();
			}
		}
		//TODO 数据文件过最大缓存期
	}
	
	/**
	 * 合计字节数
	 */
	private void countTotalBytes(){
		this.totalBytes = 0;
		Iterator<Map.Entry<String,LRUElement>> iterator = this.entrySet().iterator();
		while(iterator.hasNext() ){
			totalBytes += iterator.next().getValue().getBytes();
		}
	}
	
	public static void main(String[] args) throws FileTooBigException{
		LURCache lurCache = new LURCache("",3,100);
		for(int i=5;i>0;i--){
			if(i==2){
				lurCache.get("4");
			}
			lurCache.add(""+i, "/a/b",100l,new Date().getTime(),new byte[i]);
		}
		Iterator<Map.Entry<String,LRUElement>> iterator = lurCache.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String,LRUElement> entry = iterator.next();
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}
}
