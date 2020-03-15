package com.mikufans.manage.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class PermissionVo implements Serializable
{
    private String id;

    private String name;

    private String pId;

    private String istype;

    private String code;

    private String page;

    private String icon;

    private String zindex;

    private boolean checked;

    private boolean open;
}
