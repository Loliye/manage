package com.mikufans.manage.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * shiro  密码输入错误限制为6次 密码锁定2分钟
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher
{
    //集群中可能会导致出现验证多过5次的现象，因为AtomicInteger只能保证单节点并发
    //解决方案，利用ehcache、redis（记录错误次数）和mysql数据库（锁定）的方式处理：密码输错次数限制； 或两者结合使用
    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager)
    {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info)
    {
        String username = (String) token.getPrincipal();

        AtomicInteger retryCount = passwordRetryCache.get(username);
        //首次登陆
        if (retryCount == null)
        {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        //登陆次数超过五次
        if (retryCount.incrementAndGet() > 5)
            throw new ExcessiveAttemptsException("username:" + username + " tried to login more than 5 times in period");
        //否则走判断密码逻辑
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches)
        {
            // clear retry count  清楚ehcache中的count次数缓存
            passwordRetryCache.remove(username);

        }
        return matches;
    }

}
