package com.mikufans.manage.util;

public class Result<T>
{
    private static final Result ME = new Result();
    private String status;
    private String message;
    private T data;

    private Result()
    {
        // 单例
    }

    /**
     * 响应status和message
     *
     * @param status
     * @param message
     */
    public Result(String status, String message)
    {
        this.status = status;
        this.message = message;
    }

    /**
     * 响应status、message和result
     *
     * @param status
     * @param message
     * @param data
     */
    public Result(String status, String message, T data)
    {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static Result getInstance()
    {
        return ME;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "Result [status=" + status + ", message=" + message + ", data="
                + data + "]";
    }
}
