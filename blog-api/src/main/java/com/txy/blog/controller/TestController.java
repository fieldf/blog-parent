package com.txy.blog.controller;

import com.txy.blog.dao.pojo.SysUser;
import com.txy.blog.utils.UserThreadLocal;
import com.txy.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);
        return Result.success(null);
    }
}
