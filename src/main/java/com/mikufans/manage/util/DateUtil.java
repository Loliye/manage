package com.mikufans.manage.util;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil implements AutoCloseable, Serializable
{
    private static final long serialVersionUID = 5110771010886130754L;
    //饿汉单例
    public static DateUtil instance = new DateUtil();
    // SimpleDateFormat线程不安全的类，使用ThreadLocal,
    // 也是将共享变量变为独享，线程独享肯定能比方法独享在并发环境中能减少不少创建对象的开销。如果对性能要求比较高的情况下，一般推荐使用这种方法。
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>()
    {
        @Override
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private DateUtil()
    {
    }

    public static DateUtil getInstance()
    {
        return instance;
    }

    /**
     * @return Date
     * @throws ParseException
     * @描述：格式化String转换为Date
     */
    public static Date parse(String dateStr) throws ParseException
    {
        return threadLocal.get().parse(dateStr);
    }

    /**
     * @param date
     * @return 格式：yyyy-MM-dd HH:mm:ss
     * @描述：将date日期转换为string
     */
    public static String format(Date date)
    {
        return threadLocal.get().format(date);
    }

    //防序列化（杜绝单例对象被反序列化时重新生成对象）
    private Object readResolve() throws ObjectStreamException
    {
        return instance;
    }

    @Override
    public void close() throws Exception
    {
    }
}
