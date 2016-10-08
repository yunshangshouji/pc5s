package zhuboss.pc2server.common;

import java.net.InetSocketAddress;

public class NetUtil {
	
	public static MyAddress convet(InetSocketAddress address){
		return new MyAddress(address.getAddress().getHostAddress(), address.getPort());
	}
}
