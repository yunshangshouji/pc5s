package zhuboss.pc2server.sengine.mis.service.impl;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.rest.OperateResult;
import zhuboss.pc2server.sengine.StartSEngine;
import zhuboss.pc2server.sengine.mis.mapper.UserDomainPOMapper;
import zhuboss.pc2server.sengine.mis.mapper.UserPOMapper;
import zhuboss.pc2server.sengine.mis.po.UserDomainPO;
import zhuboss.pc2server.sengine.mis.po.UserPO;
import zhuboss.pc2server.sengine.mis.service.UserService;
import zhuboss.pc2server.sengine.webserver.AccessCfg;
import zhuboss.pc2server.sengine.webserver.EhCacheManager;
import zhuboss.pc2server.sengine.webserver.EhCacheManager.CACHE;

public class UserServiceImpl implements UserService {
	@Autowired
	UserPOMapper userPOMapper;
	@Autowired
	UserDomainPOMapper userDomainPOMapper;
	@Autowired
	EhCacheManager ehCacheManager;
	@Override
	public OperateResult<UserDomainPO> login(String appkey) {
		List<UserDomainPO> userDomainPOist = userDomainPOMapper.selectByClause(new QueryClauseBuilder()
			.andEqual("app_key", appkey)
			.andEqual("alive_flag", 1));
		if(userDomainPOist.size() == 0){
			return new OperateResult<>(OperateResult.FAIL_CODE, "appkey not valid 不存在或已过期");
		}
		
		OperateResult<UserDomainPO> result = new OperateResult<UserDomainPO>();
		result.setData(userDomainPOist.get(0));
		return result;
	}

	@Override
	public long getFlowLimit(final String userDomain) {
		if(userDomain==null) return 0l;
		return ehCacheManager.getData(CACHE.FLOW_LIMIT, userDomain, new Callable<Long>() {
				@Override
				public Long call() throws Exception {
					UserPOMapper userPOMapper =  StartSEngine.getApplicationContext().getBean(UserPOMapper.class);
					List<UserDomainPO> userDomainPOList = userDomainPOMapper.selectByClause(new QueryClauseBuilder().andEqual("user_domain", userDomain));
					if(userDomainPOList.size()==0){
						return 0l;
					}
					UserPO userPO= userPOMapper.selectByPK(userDomainPOList.get(0).getUserId());
					if(userPO == null){
						return 0l;
					}else{
						return (long)userPO.getFlowLimit1()*1024*1024;
					}
				}
			});
	}

	@Override
	public AccessCfg getAccessCfg(final String userDomain) {
		if(userDomain ==null ) return null;
		return ehCacheManager.getData(CACHE.ACCESS_CFG, userDomain, new Callable<AccessCfg>() {
			@Override
			public AccessCfg call() throws Exception {
				List<UserDomainPO> list = userDomainPOMapper.selectByClause(new QueryClauseBuilder().andEqual("user_domain", userDomain));
				AccessCfg accessCfg = new AccessCfg();
				if(list.size()>0){
					accessCfg.setWebAccessPwd(list.get(0).getWebAccessPwd());
					accessCfg.setWhiteIpList(list.get(0).getWhiteIpList());
				}
				return accessCfg;
			}
		});
	}

	@Override
	public Integer getUserId(final String userDomain) {
		if(userDomain ==null ) return null;
		return ehCacheManager.getData(CACHE.DOMAIN_USERID, userDomain, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				List<UserDomainPO> userDomainPOList = userDomainPOMapper.selectByClause(new QueryClauseBuilder().andEqual("user_domain", userDomain));
				if(userDomainPOList.size()==0){
					return null;
				}
				return userDomainPOList.get(0).getUserId();
			}
		});
	}

}
