package com.mikufans.manage.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class User implements Serializable
{
    private Integer id;

    private String username;

    private String mobile;

    private String email;

    private String password;

    private Integer insertUid;

    private Date insertTime;

    private Date updateTime;

    private Boolean isDel;

    private Boolean isJob;

    private String mcode;

    private Date sendTime;

    private Integer version;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setUsername(String username)
    {
        this.username = username==null?null:username.trim();
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile==null?null:mobile.trim();
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPassword(String password)
    {
        this.password = password==null?null:password.trim();
    }

    public void setInsertUid(Integer insertUid)
    {
        this.insertUid = insertUid;
    }

    public void setInsertTime(Date insertTime)
    {
        this.insertTime = insertTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public void setDel(Boolean del)
    {
        isDel = del;
    }

    public void setJob(Boolean job)
    {
        isJob = job;
    }

    public void setMcode(String mcode)
    {
        this.mcode = mcode;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }
}
