package com.mikufans.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@Configuration
public class ManageApplication
{

    public static void main(String[] args)
    {
//        SpringApplication springApplication = new SpringApplication(ManageApplication.class);
        SpringApplication.run(ManageApplication.class, args);
//        springApplication.setAddCommandLineProperties(false);
//        springApplication.run(args);
    }

}
