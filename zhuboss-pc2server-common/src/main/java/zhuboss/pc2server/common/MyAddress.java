package zhuboss.pc2server.common;

public class MyAddress {
	private String host;
	private int port;
	public MyAddress(){
		
	}
	public MyAddress(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	@Override
	public String toString(){
		return host + ":" + port;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
}
