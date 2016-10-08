package zhuboss.pc2server.sengine.proxycache;

import java.io.Serializable;

import org.springframework.util.Assert;

public class LRUElement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5430632846543833330L;
	
	private String requestURI;
	private long expireDate;
	private int bytes;
	private long lastModified; //for 304 check
	public LRUElement(String requestURI, long expireDate, long lastModified,int bytes) {
		Assert.hasText(requestURI);
		Assert.isTrue(bytes>0);
		this.requestURI = requestURI;
		this.expireDate = expireDate;
		this.lastModified = lastModified;
		this.bytes = bytes;
	}
	
	public String getRequestURI() {
		return requestURI;
	}
	public long getExpireDate() {
		return expireDate;
	}
	public int getBytes() {
		return bytes;
	}

	public long getLastModified() {
		return lastModified;
	}

	@Override
	public String toString() {
		return "LRUElement [requestURI=" + requestURI + ", expireDate="
				+ expireDate + ", bytes=" + bytes + ", lastModified="
				+ lastModified + "]";
	}

	
}
