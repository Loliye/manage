package com.mikufans.manage.entity;

import com.mikufans.manage.pojo.UserRoleKey;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UserRoleVo
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

    private List<UserRoleKey> userRoles;

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

    public void setUserRoles(List<UserRoleKey> userRoles)
    {
        this.userRoles = userRoles;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }
}
