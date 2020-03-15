package com.mikufans.manage.interceptor;

import com.mikufans.manage.entity.ResponseResult;
import com.mikufans.manage.pojo.User;
import com.mikufans.manage.service.UserService;
import com.mikufans.manage.util.IStatusMessage;
import com.mikufans.manage.util.ShiroFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserActionInterceptor implements HandlerInterceptor
{

    private static Logger logger = LoggerFactory.getLogger(UserActionInterceptor.class);
    //退出重定向的地址
    private final String kickoutUrl = "/toLogin";
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        logger.debug("请求到达后台方法之前调用（controller之前）");
        //securityUtils获取session信息
        //httpSession session=request.getSession();

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user != null && StringUtils.isNotEmpty(user.getMobile()) && user.getVersion() != null)
        {
            User dataUser = this.userService.findUserByMobile(user.getMobile());
            if (dataUser != null && dataUser.getVersion() != null &&
                    String.valueOf(user.getVersion()).equals(String.valueOf(dataUser.getVersion())))
                return true;
            else
            {
                SecurityUtils.getSubject().logout();
                isAjaxResponse(request,response);
            }
        }
        return false;
    }

    /**
     * 判断是否是ajax请求
     * 1如果是访问，那么给予json返回值提示
     * 2如果是普通请求，直接跳转登录页
     *
     * @param request
     * @param response
     * @return
     */
    private boolean isAjaxResponse(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        ResponseResult responseResult = new ResponseResult();
        if (ShiroFilterUtils.isAjax(request))
        {
            logger.debug(getClass().getName() + "，当前用户的信息或权限已变更，重新登录后生效！");
            responseResult.setCode(IStatusMessage.SystemStatus.UPDATE.getCode());
            responseResult.setMessage("您的信息或权限已变更，重新登录后生效");
            ShiroFilterUtils.out(response, responseResult);
        } else WebUtils.issueRedirect(request, response, kickoutUrl);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        logger.debug("请求处理之后调用；在视图渲染之前，controller处理之后。");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        logger.debug("整个请求完成之后调用。DispatcherServlet视图渲染完成之后。（主要是用于进行资源清理工作）");
    }
}
