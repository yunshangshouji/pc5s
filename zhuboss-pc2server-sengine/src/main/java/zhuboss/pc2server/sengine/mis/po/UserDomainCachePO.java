package zhuboss.pc2server.sengine.mis.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import freemarker.core.Environment;
import zhuboss.framework.mybatis.mapper.AbstractPO;

public class UserDomainCachePO extends AbstractPO {
    private Integer id;

    private Integer userDomainId;

    private String caseSensitive;

    private String howMatch;

    private String location;

    private Integer expireTime;

    private String caseArgs;

    private String expireUnit;

    private Date createDate;

    private Date modifyDate;

 	private List<String> locationList ;
    
    public synchronized List<String> getLocationList() {
    	if(locationList == null){
    		String[] locationArray = location.split("[\\s]+");
    		locationList = new ArrayList<String>();
    		for(String l : locationArray){
    			locationList.add(l.trim());
    		}
    	}
		return locationList;
	}
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserDomainId() {
        return userDomainId;
    }

    public void setUserDomainId(Integer userDomainId) {
        this.userDomainId = userDomainId;
    }

    public String getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(String caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public String getHowMatch() {
        return howMatch;
    }

    public void setHowMatch(String howMatch) {
        this.howMatch = howMatch;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public String getCaseArgs() {
        return caseArgs;
    }

    public void setCaseArgs(String caseArgs) {
        this.caseArgs = caseArgs;
    }

    public String getExpireUnit() {
        return expireUnit;
    }

    public void setExpireUnit(String expireUnit) {
        this.expireUnit = expireUnit;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}