package com.txy.blog.controller;

import com.txy.blog.dao.pojo.SysUser;
import com.txy.blog.service.LoginService;
import com.txy.blog.service.SysUserService;
import com.txy.blog.vo.Result;
import com.txy.blog.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {
//    @Autowired
//    private SysUserService sysUserService; //高内聚低耦合
    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParam loginParam) {
        // 登录 验证用户
        return loginService.login(loginParam);

    }
}
