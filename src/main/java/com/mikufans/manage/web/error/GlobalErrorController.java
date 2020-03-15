package com.mikufans.manage.web.error;

import com.mikufans.manage.util.ExceptionEnum;
import com.mikufans.manage.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/error")
public class GlobalErrorController extends AbstractErrorController
{
    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);

    private static final String ERROR_PATH = "error";

    public GlobalErrorController(ErrorAttributes errorAttributes)
    {
        super(errorAttributes);
    }

    @Override
    public String getErrorPath()
    {
        return ERROR_PATH;
    }

    @RequestMapping(produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response)
    {
        logger.debug("统一异常处理【" + getClass().getName()
                + ".errorHtml】text/html=普通请求：request=" + request);
        ModelAndView modelAndView = new ModelAndView(ERROR_PATH);
        //model 包含了异常信息
        Map<String, Object> model = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML));
        logger.info("统一异常处理【" + getClass().getName()
                + ".errorHtml】统一异常处理：model=" + model);
        // 1 获取错误状态码（也可以根据异常对象返回对应的错误信息）
        HttpStatus httpStatus = getStatus(request);
        logger.debug("统一异常处理【" + getClass().getName()
                + ".errorHtml】统一异常处理!错误状态码httpStatus：" + httpStatus);
        // 2 返回错误提示
        ExceptionEnum exceptionEnum = getMessage(httpStatus);
        Result<String> result = new Result<>(String.valueOf(exceptionEnum.getCode()), exceptionEnum.getMsg());
        // 3 将错误信息放入modelAndView中
        modelAndView.addObject("result", result);
        logger.info("统一异常处理【" + getClass().getName()
                + ".errorHtml】统一异常处理!错误信息mv：" + modelAndView);
        return modelAndView;

    }

    @RequestMapping
    @ResponseBody
    //设置响应状态码为：200，结合前端约定的规范处理。也可不设置状态码，前端ajax调用使用error函数进行控制处理
    @ResponseStatus(value = HttpStatus.OK)
    public Result<String> error(HttpServletRequest request, Exception e)
    {
        logger.info("统一异常处理【" + getClass().getName()
                + ".error】text/html=普通请求：request=" + request);
        /** model对象包含了异常信息 */
        Map<String, Object> model = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.TEXT_HTML));
        logger.info("统一异常处理【" + getClass().getName()
                + ".error】统一异常处理：model=" + model);
        // 1 获取错误状态码（也可以根据异常对象返回对应的错误信息）
        HttpStatus httpStatus = getStatus(request);
        logger.debug("统一异常处理【" + getClass().getName()
                + ".error】统一异常处理!错误状态码httpStatus：" + httpStatus);
        // 2 返回错误提示
        ExceptionEnum ee = getMessage(httpStatus);
        Result<String> result = new Result<String>(
                String.valueOf(ee.getType()), ee.getCode(), ee.getMsg());
        // 3 将错误信息返回
        //		ResponseEntity
        logger.info("统一异常处理【" + getClass().getName()
                + ".error】统一异常处理!错误信息result：" + result);
        return result;
    }


    private boolean isIncludeStackTrace(HttpServletRequest request, MediaType produce)
    {
        ErrorProperties.IncludeStacktrace includeStacktrace = new ErrorProperties().getIncludeStacktrace();
        if (includeStacktrace == ErrorProperties.IncludeStacktrace.ALWAYS)
            return true;
        if (includeStacktrace == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM)
            return getTraceParameter(request);
        return false;
    }

    private ExceptionEnum getMessage(HttpStatus httpStatus)
    {
        if (httpStatus.is4xxClientError())
        {
            // 4开头的错误状态码
            if (400 == httpStatus.BAD_REQUEST.value())
            {
                return ExceptionEnum.BAD_REQUEST;
            } else if (403 == httpStatus.FORBIDDEN.value())
            {
                return ExceptionEnum.BAD_REQUEST;
            } else if (404 == httpStatus.NOT_FOUND.value())
            {
                return ExceptionEnum.NOT_FOUND;
            }
        } else if (httpStatus.is5xxServerError())
        {
            // 5开头的错误状态码
            if (500 == httpStatus.INTERNAL_SERVER_ERROR.value())
            {
                return ExceptionEnum.SERVER_EPT;
            }
        }
        // 统一返回：未知错误
        return ExceptionEnum.UNKNOW_ERROR;
    }

}
