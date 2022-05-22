package com.txy.blog.common.aop;

import java.lang.annotation.*;

// TYPE代表可以放在类上面，METHOD代表可以放在方法上
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    String module() default "";
    String operator() default "";
}
