package com.mikufans.manage.service;

import com.mikufans.manage.entity.PermissionVo;
import com.mikufans.manage.entity.RoleVo;
import com.mikufans.manage.pojo.Permission;
import com.mikufans.manage.pojo.Role;

import java.util.List;

public interface AuthService
{

    int addPermission(Permission permission);

    List<Permission> permList();

    int updatePerm(Permission permission);

    Permission getPermission(int id);

    String delPermission(int id);

    List<Role> roleList();

    List<PermissionVo> findPerms();

    String addRole(Role role, String permIds);

    RoleVo findRoleAndPerms(Integer id);

    String updateRole(Role role, String permIds);

    String delRole(int id);

    List<Role> getRoles();

    List<Role> getRoleByUser(Integer userId);

    List<Permission> findPermsByRoleId(Integer id);

    List<PermissionVo> getUserPerms(Integer id);
}
