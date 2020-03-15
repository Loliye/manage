package com.mikufans.manage.util;

import com.mikufans.manage.service.AuthService;
import lombok.ToString;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * 封装DTO分页数据
 */
@ToString
public class PageDataResult
{
    private Integer totals;

    private List<?> list;

    private Integer code = 200;

    public PageDataResult()
    {
    }

    public PageDataResult(Integer totals,
                          List<?> list)
    {
        this.totals = totals;
        this.list = list;
    }

    public Integer getTotals()
    {
        return totals;
    }

    public void setTotals(Integer totals)
    {
        this.totals = totals;
    }

    public List<?> getList()
    {
        return list;
    }

    public void setList(List<?> list)
    {
        this.list = list;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

}
