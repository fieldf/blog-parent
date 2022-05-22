package com.txy.blog.service;

import com.txy.blog.dao.pojo.SysUser;
import com.txy.blog.vo.Result;
import com.txy.blog.vo.params.LoginParam;
import org.springframework.transaction.annotation.Transactional;


public interface LoginService {
    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    /**
     * 注册
     * @param loginParam
     * @return
     */
    Result register(LoginParam loginParam);
}
