package com.mikufans.manage.entity;

import com.mikufans.manage.util.IStatusMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class ResponseResult implements Serializable
{
    private String code;
    private String message;
    private Object obj;

    public ResponseResult()
    {
        this.code = IStatusMessage.SystemStatus.SUCCESS.getCode();
        this.message = IStatusMessage.SystemStatus.SUCCESS.getMessage();
    }

    public ResponseResult(IStatusMessage statusMessage)
    {
        this.code = statusMessage.getCode();
        this.message = statusMessage.getMessage();

    }

}
