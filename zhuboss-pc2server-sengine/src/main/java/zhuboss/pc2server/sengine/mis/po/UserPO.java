package zhuboss.pc2server.sengine.mis.po;

import java.util.Date;
import zhuboss.framework.mybatis.mapper.AbstractPO;

public class UserPO extends AbstractPO {
    private Integer id;

    private String email;

    private String passwd;

    private String userLevel;

    private String userName;

    private String canSetUserName;

    private Integer flowLimit1;

    private Integer flowLimit2;

    private Date createDate;

    private Date modifyDate;

    private String registerFlag;

    private String verifyCode;

    private String aliveFlag;

    private String registerIp;

    private Date lastLoginTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCanSetUserName() {
        return canSetUserName;
    }

    public void setCanSetUserName(String canSetUserName) {
        this.canSetUserName = canSetUserName;
    }

    public Integer getFlowLimit1() {
        return flowLimit1;
    }

    public void setFlowLimit1(Integer flowLimit1) {
        this.flowLimit1 = flowLimit1;
    }

    public Integer getFlowLimit2() {
        return flowLimit2;
    }

    public void setFlowLimit2(Integer flowLimit2) {
        this.flowLimit2 = flowLimit2;
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

    public String getRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(String registerFlag) {
        this.registerFlag = registerFlag;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getAliveFlag() {
        return aliveFlag;
    }

    public void setAliveFlag(String aliveFlag) {
        this.aliveFlag = aliveFlag;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}