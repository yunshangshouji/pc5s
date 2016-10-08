package zhuboss.pc2server.common;

import java.util.Date;

public class CacheResource {
	private String uri;
	private Integer bytes;
	private Date expireDate;
	
	public CacheResource(){
		
	}
	public CacheResource(String uri, Integer bytes, Date expireDate) {
		this.uri = uri;
		this.bytes = bytes;
		this.expireDate = expireDate;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public Integer getBytes() {
		return bytes;
	}
	public void setBytes(Integer bytes) {
		this.bytes = bytes;
	}
	public Date getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
}
