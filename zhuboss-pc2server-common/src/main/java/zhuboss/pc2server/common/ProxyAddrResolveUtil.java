package zhuboss.pc2server.common;

import java.net.MalformedURLException;
import java.net.URL;

public class ProxyAddrResolveUtil {
	
	public static Result resolve(String local){
		Result result = new Result();
		if(!local.startsWith("http")){
			local = "http://"+local;
		}
		URL url;
		try {
			url = new URL(local);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		
		result.setHttps(url.getProtocol().equals("https"));
		result.setHost(url.getHost());
		result.setPort(url.getPort()==-1? (result.isHttps?443:80):url.getPort());
		
		return result;
	}
	
	public static class Result{
		private boolean isHttps;
		private String host;
		private int port;
		public boolean isHttps() {
			return isHttps;
		}
		public void setHttps(boolean isHttps) {
			this.isHttps = isHttps;
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
}
