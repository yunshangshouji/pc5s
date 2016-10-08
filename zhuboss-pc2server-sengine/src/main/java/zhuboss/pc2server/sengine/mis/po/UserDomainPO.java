package zhuboss.pc2server.sengine.mis.po;

import java.util.Date;
import zhuboss.framework.mybatis.mapper.AbstractPO;

public class UserDomainPO extends AbstractPO {
    private Integer id;

    private Integer userId;

    private String userDomain;

    private String appKey;

    private String webAccessPwd;

    private String whiteIpList;

    private String aliveFlag;

    private Date createDate;

    private Date modifyDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getWebAccessPwd() {
        return webAccessPwd;
    }

    public void setWebAccessPwd(String webAccessPwd) {
        this.webAccessPwd = webAccessPwd;
    }

    public String getWhiteIpList() {
        return whiteIpList;
    }

    public void setWhiteIpList(String whiteIpList) {
        this.whiteIpList = whiteIpList;
    }

    public String getAliveFlag() {
        return aliveFlag;
    }

    public void setAliveFlag(String aliveFlag) {
        this.aliveFlag = aliveFlag;
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