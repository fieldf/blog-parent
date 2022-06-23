package com.txy.blog.admin.service;

import com.txy.blog.admin.mapper.SysUserMapper;
import com.txy.blog.admin.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser==null) {
            sysUser=new SysUser();
            sysUser.setNickname("test");
        }
        return sysUser;
    }
}
