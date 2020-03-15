package com.mikufans.manage.config;

import com.mikufans.manage.filter.KickoutSessionFilter;
import com.mikufans.manage.shiro.RetryLimitHashedCredentialsMatcher;
import com.mikufans.manage.shiro.ShiroRealm;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class ShiroConfig
{
    private static final Logger logger = LoggerFactory.getLogger(ShiroConfig.class);

    /**
     * shiroFIlterFactoryBean 处理拦截志愿文件过滤器
     * 1配置shiro安全管理器接口
     * 2shiro连接约束配置filterChainDefinitions
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager)
    {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        logger.debug("-----------------Shiro拦截器工厂类注入开始");
        //配置shiro安全管理器SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //添加kickoout认证
        HashMap<String, Filter> map = new HashMap<>();
        map.put("kickout", kickoutSessionFilter());
        shiroFilterFactoryBean.setFilters(map);

        //指定要求登陆时的连接
        shiroFilterFactoryBean.setLoginUrl("/toLogin");
        //登陆成功后要跳转的连接
        shiroFilterFactoryBean.setSuccessUrl("/home");
        //未授权时的跳转页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");

        // filterChainDefinitions拦截器=map必须用：LinkedHashMap，因为它必须保证有序
        Map<String,String> filterChainDefinitionMap=new LinkedHashMap<>();
        filterChainDefinitionMap.put("/logout","logout");
        //配置记住我或认证通过可以访问的地址
        filterChainDefinitionMap.put("/user/userList", "user");
        filterChainDefinitionMap.put("/", "user");
        //
        //		// 配置不会被拦截的链接 从上向下顺序判断
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/css/*", "anon");
        filterChainDefinitionMap.put("/js/*", "anon");
        filterChainDefinitionMap.put("/js/*/*", "anon");
        filterChainDefinitionMap.put("/js/*/*/*", "anon");
        filterChainDefinitionMap.put("/images/*/**", "anon");
        filterChainDefinitionMap.put("/layui/*", "anon");
        filterChainDefinitionMap.put("/layui/*/**", "anon");
        filterChainDefinitionMap.put("/treegrid/*", "anon");
        filterChainDefinitionMap.put("/treegrid/*/*", "anon");
        filterChainDefinitionMap.put("/fragments/*", "anon");
        filterChainDefinitionMap.put("/layout", "anon");

        filterChainDefinitionMap.put("/user/sendMsg", "anon");
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/home", "anon");
        //		/*filterChainDefinitionMap.put("/page", "anon");
        //		filterChainDefinitionMap.put("/channel/record", "anon");*/
        filterChainDefinitionMap.put("/user/delUser", "authc,perms[usermanage]");
        //		//add操作，该用户必须有【addOperation】权限
        ////		filterChainDefinitionMap.put("/add", "perms[addOperation]");
        //
        //		// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问【放行】-->
        filterChainDefinitionMap.put("/**", "kickout,authc");
        filterChainDefinitionMap.put("/*/*", "authc");
        filterChainDefinitionMap.put("/*/*/*", "authc");
        filterChainDefinitionMap.put("/*/*/*/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        logger.debug("-----------------Shiro拦截器工厂类注入成功");
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager()
    {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(shiroRealm());
        //注入ehcache缓存管理器
        securityManager.setCacheManager(ehCacheManager());
        //注入session管理器
        securityManager.setSessionManager(sessionManager());
        //注入cookie记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    @Bean
    public ShiroRealm shiroRealm()
    {
        ShiroRealm shiroRealm = new ShiroRealm();
        //使用自定义的CredentialsMatcher进行密码检验和输入错误次数限制
        shiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return shiroRealm;
    }

    @Bean
    public EhCacheManager ehCacheManager()
    {
        logger.debug("=====shiro整合ehcache缓存：ShiroConfig.getEhCacheManager()");
        EhCacheManager ehCacheManager = new EhCacheManager();
        CacheManager cacheManager = CacheManager.getCacheManager("shiro");
        if (cacheManager == null)
        {
            try
            {
                cacheManager = CacheManager.create(ResourceUtils.getInputStreamForPath("classpath:config/ehcache.xml"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }

    /**
     * 凭证匹配器（由于密码由shiro的SimpleAuthenticationInfo进行处理
     * 所以修改下doGetAuthenticationInfo中的代码，更改密码生成规则和检验的逻辑）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher()
    {
        HashedCredentialsMatcher hashedCredentialsMatcher = new RetryLimitHashedCredentialsMatcher(ehCacheManager());
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1);//迭代次数
        return hashedCredentialsMatcher;
    }

    /**
     * sessionManager添加session缓存操作dao
     *
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager()
    {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //        sessionManager.setCacheManager(ehCacheManager());
        sessionManager.setSessionDAO(enterpriseCacheSessionDAO());
        sessionManager.setSessionIdCookie(sessionIdCookie());
        return sessionManager;
    }


    @Bean
    public EnterpriseCacheSessionDAO enterpriseCacheSessionDAO()
    {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        //添加缓存管理器
        //        enterpriseCacheSessionDAO.setCacheManager(ehCacheManager());
        //添加ehcache活跃缓存名称（必须和ehcache名称一致）
        enterpriseCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        return enterpriseCacheSessionDAO;
    }

    @Bean
    public SimpleCookie sessionIdCookie()
    {
        SimpleCookie simpleCookie = new SimpleCookie();
        //无法通过城程序（js脚本、applet等）将无法读取到cookie的信息，防止xss攻击
        simpleCookie.setHttpOnly(true);
        simpleCookie.setName("SHRIOSESSIONID");
        simpleCookie.setMaxAge(86400);
        return simpleCookie;
    }

    @Bean
    public SimpleCookie remeberMeCookie()
    {
        logger.debug("记住我，设置cookie过期时间！");
        //cookie名字，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //记住我cookie生效时间3天，单位s
        simpleCookie.setMaxAge(86400);
        return simpleCookie;
    }

    /**
     * 配置cookie记住我管理器
     *
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager()
    {
        logger.debug("配置cookies记住我管理器！");
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(remeberMeCookie());
        return cookieRememberMeManager;
    }

    public KickoutSessionFilter kickoutSessionFilter()
    {
        KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
        //使用cacheManager 获取相应的cache来缓存用户登陆的会话  用户保存-会话之间的关系的
        //这里我们还是用之前shiro使用的ehcache实现的cacheManager()缓存管理
        //也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
        kickoutSessionFilter.setCacheManager(ehCacheManager());
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionFilter.setSessionManager(sessionManager());
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
        kickoutSessionFilter.setKickoutAfter(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        kickoutSessionFilter.setMaxSession(1);
        //被踢出后重定向到的地址；
        kickoutSessionFilter.setKickoutUrl("/toLogin?kickout=1");
        return kickoutSessionFilter;

    }

    /**
     * @return
     * @描述：开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * </br>Enable Shiro Annotations for Spring-configured beans. Only run after the lifecycleBeanProcessor(保证实现了Shiro内部lifecycle函数的bean执行) has run
     * </br>不使用注解的话，可以注释掉这两个配置
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator()
    {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor()
    {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

}
