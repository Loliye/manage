package com.mikufans.manage.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikufans.manage.entity.ResponseResult;
import com.mikufans.manage.pojo.User;
import com.mikufans.manage.util.IStatusMessage;
import com.mikufans.manage.util.ShiroFilterUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Setter;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;


public class KickoutSessionFilter extends AccessControlFilter
{

    private static final Logger logger = LoggerFactory.getLogger(KickoutSessionFilter.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String kickoutUrl;//踢出后的地址
    private boolean kickoutAfter = false;//踢出之前登陆的/之后登录的用户  默认false提出之前登陆的用户
    private int maxSession = 1;//同一个账户最大会话数
    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;

    public static void out(ServletResponse response, ResponseResult result)
    {
        PrintWriter out = null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        try
        {
            out = response.getWriter();
            logger.error("用户在线数量限制【KickoutSessionFilter.out】响应json信息成功");
        } catch (IOException e)
        {
            logger.error("用户在线数量限制【KickoutSessionFilter.out】响应json信息出错", e);
            e.printStackTrace();
        } finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }


    }

    public void setKickoutUrl(String kickoutUrl)
    {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter)
    {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession)
    {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    // 设置Cache的key的前缀
    public void setCacheManager(CacheManager cacheManager)
    {
        //必须和ehcache缓存配置中的缓存name一致
        this.cache = cacheManager.getCache("shiro-activeSessionCache");
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception
    {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception
    {
        Subject subject = getSubject(request, response);
        //没有登陆授权 且没有记住我
        if (!subject.isAuthenticated() && !subject.isRemembered())
        {
            //如果没有登陆，直接进行之后的流程
            ResponseResult responseResult = new ResponseResult();
            if (ShiroFilterUtils.isAjax(request))
            {
                logger.debug(getClass().getName() + "当前用户已经再其他地方登陆，并且是Ajax请求");
                responseResult.setCode(IStatusMessage.SystemStatus.MANY_LOGINS.getCode());
                responseResult.setMessage("您已在别处登录，请您修改密码或重新登录");
                out(response, responseResult);
                return false;
            } else return true;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        logger.debug("===当前的请求的uri：==={}", path);
        String contextPath = req.getContextPath();
        logger.debug("===当前请求的域名或ip+端口：==" + contextPath);

        //放行登陆
        if (path.equals("/toLogin"))
            return true;

        Session session = subject.getSession();
        logger.debug("==session时间设置：" + String.valueOf(session.getTimeout())
                + "===========");

        User user = (User) subject.getPrincipal();
        String username = user.getUsername();
        logger.debug("===当前用户username：==" + username);
        Serializable sessionId = session.getId();
        logger.debug("===当前用户sessionId：==" + sessionId);
        Deque<Serializable> deque = cache.get(username);
        logger.debug("===当前deque：==" + deque);

        if (deque == null)
        {
            deque = new ArrayDeque<>();
        }

        //没有sessionid 且用户没有被踢出 入队列
        if (!deque.contains(sessionId) && session.getAttribute("kickout") == null)
        {
            deque.push(sessionId);
            cache.put(username, deque);
        }

        //如果队列里的sessionId书超出最大会话数，开始踢人
        while (deque.size() > maxSession)
        {
            logger.debug("===deque队列长度：==" + deque.size());
            Serializable kickoutSessionId = null;
            //是否提出来后登陆的，默认是false：即后来者登陆用户踢出前者登陆的用户
            if (kickoutAfter)
                kickoutSessionId = deque.removeFirst();//踢出前者
            else kickoutSessionId = deque.removeLast();//提出后者

            //更新
            cache.put(username, deque);
            //是否踢出  kickout表示提出
            Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
            if (kickoutSession != null)
                kickoutSession.setAttribute("kickout", true);

        }

        //如果被踢出了  直接退出，重定向到提出后的地址
        if ((Boolean) session.getAttribute("kickout") != null && (Boolean) session.getAttribute("kickout"))
        {
            subject.logout();
            saveRequest(request);
            logger.debug("==踢出后用户重定向的路径kickoutUrl:" + kickoutUrl);
            //ajax请求
            //重定向
            //            WebUtils.issueRedirect(request,response,kickoutUrl);
            return isAjaxResponse(request,response);
        }
        return true;

    }

    private Boolean isAjaxResponse(ServletRequest request, ServletResponse response) throws IOException
    {
        // ajax请求
        /**
         * 判断是否已经踢出
         * 1.如果是Ajax 访问，那么给予json返回值提示。
         * 2.如果是普通请求，直接跳转到登录页
         */
        //判断是不是Ajax请求
        ResponseResult responseResult = new ResponseResult();
        if (ShiroFilterUtils.isAjax(request))
        {
            logger.debug(getClass().getName() + "当前用户已经在其他地方登录，并且是Ajax请求！");
            responseResult.setCode(IStatusMessage.SystemStatus.MANY_LOGINS.getCode());
            responseResult.setMessage("您已在别处登录，请您修改密码或重新登录");
            out(response, responseResult);
        } else
        {
            // 重定向
            WebUtils.issueRedirect(request, response, kickoutUrl);
        }
        return false;
    }

}

