package zhuboss.pc2server.sengine.mis.service;

import zhuboss.framework.rest.OperateResult;
import zhuboss.pc2server.sengine.mis.po.UserDomainPO;
import zhuboss.pc2server.sengine.webserver.AccessCfg;

public interface UserService {
	OperateResult<UserDomainPO> login(String appkey);
	
	/**
	 * 基于缓存的流量查询
	 * @return
	 */
	long getFlowLimit(String userDomain);
	
	/**
	 * 基于缓存的访问控制查询
	 * @return
	 */
	AccessCfg getAccessCfg(String userDomain);
	
	/**
	 * 获取用户ID
	 * @param userDomain
	 * @return
	 */
	Integer getUserId(String userDomain);
}
