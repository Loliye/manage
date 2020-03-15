package com.mikufans.manage.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikufans.manage.entity.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.html.HTMLTableCaptionElement;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ShiroFilterUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ShiroFilterUtils.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 判断是否为ajax请求
     * @param request
     * @return
     */
    public static boolean isAjax(ServletRequest request)
    {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header))
        {
            logger.debug("shiro工具类【ShiroFilterUtils.isAjax】当前请求,为Ajax请求");
            return Boolean.TRUE;
        }
        logger.debug("shiro工具类【ShiroFilterUtils.isAjax】当前请求,非Ajax请求");
        return Boolean.FALSE;
    }

    public static void out(HttpServletResponse response, ResponseResult result)
    {
        PrintWriter out=null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        try
        {
            out=response.getWriter();
            out.println(objectMapper.writeValueAsString(result));
            logger.error("用户在线数量限制【ShiroFilterUtils.out】响应json信息成功");
        } catch (IOException e)
        {
            logger.error("用户在线数量限制【ShiroFilterUtils.out】响应json信息出错", e);
            e.printStackTrace();
        }
        finally
        {
            if(out!=null)
            {
                out.flush();
                out.close();
            }
        }
    }


}
