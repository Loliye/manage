package com.mikufans.manage.pojo;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.Date;

@Getter
@ToString
public class Permission
{
    private Integer id;

    private String name;

    private Integer pid;

    private Integer zindex;

    private Integer istype;

    private String descpt;

    private String code;

    private String icon;

    private String page;

    private Date insertTime;

    private Date updateTime;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name==null?null:name.trim();
    }

    public void setPid(Integer pid)
    {
        this.pid = pid;
    }

    public void setZindex(Integer zindex)
    {
        this.zindex = zindex;
    }

    public void setIstype(Integer istype)
    {
        this.istype = istype;
    }

    public void setDescpt(String descpt)
    {
        this.descpt = descpt==null?null:descpt.trim();
    }

    public void setCode(String code)
    {
        this.code = code==null?null:code.trim();
    }

    public void setIcon(String icon)
    {
        this.icon = icon==null?null:icon.trim();
    }

    public void setPage(String page)
    {
        this.page = page==null?null:page.trim();
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
