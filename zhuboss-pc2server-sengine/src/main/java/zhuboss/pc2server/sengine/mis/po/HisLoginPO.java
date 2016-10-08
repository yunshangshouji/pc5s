package zhuboss.pc2server.sengine.mis.po;

import java.util.Date;
import zhuboss.framework.mybatis.mapper.AbstractPO;

public class HisLoginPO extends AbstractPO {
    private Integer id;

    private Integer userId;

    private Date logDate;

    private String ip;

    private String params;

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

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}