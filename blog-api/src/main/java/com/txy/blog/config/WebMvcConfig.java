package com.txy.blog.config;

import com.txy.blog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 跨域配置
        // 我后端所有的接口都允许http://182.92.10.237来访问
        // 客户端访问前端8080,点击一些东西会访问一些路径，通过nginx代理到我的8888端口。

        // 博客get请求正常，post报错；管理系统正常
//        registry.addMapping("/**")
//                .allowedOrigins("http://182.92.10.237","null")
//                .allowedOrigins("http://localhost:8080")
//                .allowedMethods("POST","GET","PUT","OPTIONS","DELETE")
//                .maxAge(3600)
//                .allowCredentials(true);

        // 博客正常访问，管理系统进不去
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截test接口，后续实际遇到需要拦截的接口时，再配置为真正的拦截接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test")
                .addPathPatterns("/comments/create/change") // 发布评论
                .addPathPatterns("/articles/publish"); // 发布文章
//                .addPathPatterns("/**").excludePathPatterns("/login").excludePathPatterns("/register");
    }
}
