package com.mikufans.manage.entity;

import com.mikufans.manage.pojo.RolePermissionKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class RoleVo
{
    private Integer id;

    private String roleName;

    private String descpt;

    private String code;

    private Integer insertUid;

    private String insertTime;
    //角色下的权限ids
    private List<RolePermissionKey> rolePerms;

}
