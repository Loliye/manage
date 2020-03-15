package com.mikufans.manage.pojo;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@ToString
public class Role implements Serializable
{
    private Integer id;

    private String roleName;

    private String descpt;

    private String code;

    private Integer insertUid;

    private Date insertTime;

    private Date updateTime;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public void setDescpt(String descpt)
    {
        this.descpt = descpt == null ? null : descpt.trim();
    }

    public void setCode(String code)
    {
        this.code = code == null ? null : code.trim();
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
}
