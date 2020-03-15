package com.mikufans.manage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class UserRoleDTO
{
    private Integer id;

    private String username;

    private String mobile;

    private String email;

    private String password;

    private Integer insertUid;

    private String insertTime;

    private String updateTime;

    private boolean isDel;

    private boolean isJob;

    private String roleNames;

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
        this.email = email==null?null:email.trim();
    }

    public void setPassword(String password)
    {
        this.password = password==null?null:password.trim();
    }

    public void setInsertUid(Integer insertUid)
    {
        this.insertUid = insertUid;
    }

    public void setInsertTime(String insertTime)
    {
        this.insertTime = insertTime;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public void setDel(boolean del)
    {
        isDel = del;
    }

    public void setJob(boolean job)
    {
        isJob = job;
    }

    public void setRoleNames(String roleNames)
    {
        this.roleNames = roleNames;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }


    public Integer getId()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }

    public String getMobile()
    {
        return mobile;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPassword()
    {
        return password;
    }

    public Integer getInsertUid()
    {
        return insertUid;
    }

    public String getInsertTime()
    {
        return insertTime==null?"":insertTime.substring(0,insertTime.length()-2);
    }

    public String getUpdateTime()
    {
        return updateTime==null?"":updateTime.substring(0,updateTime.length()-2);
    }

    public boolean isDel()
    {
        return isDel;
    }

    public boolean isJob()
    {
        return isJob;
    }

    public String getRoleNames()
    {
        return roleNames;
    }

    public Integer getVersion()
    {
        return version;
    }
}
