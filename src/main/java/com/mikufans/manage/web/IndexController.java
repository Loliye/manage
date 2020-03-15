package com.mikufans.manage.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController
{
    private static final Logger logger = LoggerFactory
            .getLogger(IndexController.class);

    @RequestMapping("/index")
    public String index()
    {
        logger.debug("-------------index------------");
        return "index";
    }

    @RequestMapping("/home")
    public String toHome()
    {
        logger.debug("===111-------------home------------");
        return "home";
    }

    @RequestMapping("/login")
    public String toLogin()
    {
        logger.debug("===111-------------login------------");
        return "login";
    }

    @RequestMapping("/{page}")
    public String toPage(@PathVariable("page") String page)
    {
        logger.debug("-------------toindex------------" + page);
        return page;
    }
}
