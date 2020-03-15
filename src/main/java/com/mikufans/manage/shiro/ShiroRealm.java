package com.mikufans.manage.shiro;

import com.mikufans.manage.dao.UserMapper;
import com.mikufans.manage.pojo.Permission;
import com.mikufans.manage.pojo.Role;
import com.mikufans.manage.pojo.User;
import com.mikufans.manage.service.AuthService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiroRealm extends AuthorizingRealm
{

    private static final Logger logger = LoggerFactory.getLogger(ShiroRealm.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthService authService;


    /**
     * 登陆验证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException
    {
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        logger.info("用户登录认证：验证当前Subject时获取到token为：" + ReflectionToStringBuilder
                .toString(userToken, ToStringStyle.MULTI_LINE_STYLE));

        String mobile = userToken.getUsername();
        User user = userMapper.findUserByMobile(mobile);
        logger.debug("用户登陆认证！用户信息user:{}", user);
        if (user == null)
            return null;
        else
        {
            return new SimpleAuthenticationInfo(user, DigestUtils.md5Hex(user.getPassword()),getName());
        }
    }

    /**
     * 授予角色和权限
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
        logger.debug("授予角色和权限");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //获取当前登陆用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (user.getMobile().equals("18516596566"))
        {
            //超级管理员，添加所有角色、权限
            authorizationInfo.addRole("");
            authorizationInfo.addStringPermission("");
        } else
        {
            //普通用户，查询用户橘色，根据角色查询权限
            Integer userId = user.getId();
            List<Role> roles = this.authService.getRoleByUser(userId);
            if (roles != null && roles.size() != 0)
            {
                for (Role role : roles)
                {
                    authorizationInfo.addRole(role.getCode());
                    //加载角色相应的权限
                    List<Permission> permissions = this.authService.findPermsByRoleId(role.getId());
                    if (permissions != null && permissions.size() != 0)
                    {
                        for (Permission permission : permissions)
                        {
                            authorizationInfo.addStringPermission(permission.getCode());
                        }
                    }
                }
            }
        }
        return authorizationInfo;
    }


    public void clearCacheAuth()
    {
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }

}
