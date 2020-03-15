package com.mikufans.manage.entity;

import com.alibaba.druid.filter.AutoLoad;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserSearchDTO
{
    private Integer page;

    private Integer limit;

    private String uname;

    private String umobile;

    private String insertTimeStart;

    private String insertTimeEnd;
}
