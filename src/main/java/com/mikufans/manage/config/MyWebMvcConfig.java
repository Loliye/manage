package com.mikufans.manage.config;

import com.mikufans.manage.interceptor.UserActionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 自定义静态资源映射路径和静态资源文件存放路径
 */
@Configuration
public class MyWebMvcConfig extends WebMvcConfigurerAdapter
{
    /**
     * 再spring添加拦截器之前创建拦截器对象，这样就能再spring映射这个拦截器前，把拦截器中的依赖对象给初始化完成了
     *
     * @return
     */
    @Bean
    public UserActionInterceptor userActionInterceptor()
    {
        return new UserActionInterceptor();
    }

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(userActionInterceptor())
                .addPathPatterns("/user/**", "/auth/**")
                .excludePathPatterns("/user/sendMsg", "/user/login");
        super.addInterceptors(registry);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry)
//    {
//        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
//        registry.addResourceHandler("/layui/**")
//                .addResourceLocations("/layui/");
//        super.addResourceHandlers(registry);
//    }
}
