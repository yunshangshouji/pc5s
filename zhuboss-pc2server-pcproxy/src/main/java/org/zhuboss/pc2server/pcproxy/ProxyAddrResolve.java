package org.zhuboss.pc2server.pcproxy;

import zhuboss.pc2server.common.ProxyAddrResolveUtil;
import zhuboss.pc2server.common.ProxyAddrResolveUtil.Result;

public class ProxyAddrResolve {
	
	public static Result resolve(){
		String local = System.getProperty(org.zhuboss.pc2server.pcproxy.Constants.PROXY_ADDR);
		return ProxyAddrResolveUtil.resolve(local);
	}
	
}
