package com.txy.blog.handler;

import com.txy.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 对加了@Controller注解的方法进行拦截处理 AOP实现
@ControllerAdvice
public class AllExceptionHandler {
    @ExceptionHandler(Exception.class)//进行异常处理，处理Exception.class的异常
    @ResponseBody //返回json数据
    public Result doException(Exception ex) {
        ex.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}
