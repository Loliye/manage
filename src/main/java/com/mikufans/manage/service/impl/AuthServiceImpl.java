package com.mikufans.manage.service.impl;

import com.mikufans.manage.dao.PermissionMapper;
import com.mikufans.manage.dao.RoleMapper;
import com.mikufans.manage.dao.RolePermissionMapper;
import com.mikufans.manage.entity.PermissionVo;
import com.mikufans.manage.entity.RoleVo;
import com.mikufans.manage.pojo.Permission;
import com.mikufans.manage.pojo.Role;
import com.mikufans.manage.pojo.RolePermissionKey;
import com.mikufans.manage.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService
{
    private static final Logger logger = LoggerFactory
            .getLogger(AuthServiceImpl.class);
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;


    @Override
    public int addPermission(Permission permission)
    {
        return this.permissionMapper.insert(permission);
    }

    @Override
    public RoleVo findRoleAndPerms(Integer id)
    {
        return null;
    }

    @Override
    public List<Permission> permList()
    {
        return this.permissionMapper.findAll();
    }

    @Override
    public int updatePerm(Permission permission)
    {
        return this.permissionMapper.updateByPrimaryKeySelective(permission);
    }

    @Override
    public Permission getPermission(int id)
    {
        return this.permissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public String delPermission(int id)
    {
        List<Permission> childPerm = this.permissionMapper.findChildPerm(id);
        if (childPerm != null && childPerm.size() > 0)
        {
            return "删除失败，请您先删除该权限的子节点";
        }
        if (this.permissionMapper.deleteByPrimaryKey(id) > 0)
        {
            return "ok";
        } else
        {
            return "删除失败，请您稍后再试";
        }
    }

    @Override
    public List<Role> roleList()
    {
        return this.roleMapper.findList();
    }

    @Override
    public List<PermissionVo> findPerms()
    {
        return this.permissionMapper.findPerms();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {RuntimeException.class, Exception.class})
    public String addRole(Role role, String permIds)
    {
        this.roleMapper.insert(role);
        int roleId = role.getId();
        String[] arrays = permIds.split(",");
        logger.debug("权限id =arrays=" + arrays.toString());
        setRolePerms(roleId, arrays);
        return "ok";
    }

    /**
     * 给当前角色设置权限
     *
     * @param roleId
     * @param arrays
     */
    private void setRolePerms(int roleId, String[] arrays)
    {
        for (String permid : arrays)
        {
            RolePermissionKey rolePermissionKey = new RolePermissionKey();
            rolePermissionKey.setRoleId(roleId);
            rolePermissionKey.setPermitId(Integer.valueOf(permid));
            this.rolePermissionMapper.insert(rolePermissionKey);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {RuntimeException.class, Exception.class})

    public String updateRole(Role role, String permIds)
    {
        int roleId = role.getId();
        String[] arrays = permIds.split(",");
        logger.debug("权限id =arrays=" + arrays.toString());
        //1 更新角色表数据
        int num = this.roleMapper.updateByPrimaryKeySelective(role);
        if (num < 1)
        {
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "操作失败";
        }
        //2 删除新得角色权限数据
        batchDelRolePerms(roleId);
        setRolePerms(roleId, arrays);
        return "ok";
    }

    /**
     * 批量删除角色权限中间表数据
     *
     * @param roleId
     */
    private void batchDelRolePerms(int roleId)
    {
        List<RolePermissionKey> rolePermissionKeys = this.rolePermissionMapper.findByRole(roleId);
        if (rolePermissionKeys != null && rolePermissionKeys.size() > 0)
        {
            for (RolePermissionKey key : rolePermissionKeys)
                this.rolePermissionMapper.deleteByPrimaryKey(key);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 30000, rollbackFor = {RuntimeException.class, Exception.class})
    public String delRole(int id)
    {
        //1.删除角色对应的权限
        batchDelRolePerms(id);
        //2.删除角色
        int num = this.roleMapper.deleteByPrimaryKey(id);
        if (num < 1)
        {
            //事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "操作失败";
        }
        return "ok";
    }

    @Override
    public List<Role> getRoles()
    {
        return this.roleMapper.getRoles();
    }

    @Override
    public List<Role> getRoleByUser(Integer userId)
    {
        return this.roleMapper.getRoleByUserId(userId);
    }

    @Override
    public List<Permission> findPermsByRoleId(Integer id)
    {
        return this.permissionMapper.findPermsByRole(id);
    }

    @Override
    public List<PermissionVo> getUserPerms(Integer id)
    {
        return this.permissionMapper.getUserPerms(id);
    }
}
