package com.txy.blog.common.cache;


import java.lang.annotation.*;

@Target({ElementType.METHOD}) // 作用在方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    long expire() default 1 * 60 * 1000;

    // 缓存标识key
    String name() default "";

}