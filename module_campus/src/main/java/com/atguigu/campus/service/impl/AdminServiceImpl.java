package com.atguigu.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.campus.pojo.Admin;
import com.atguigu.campus.service.AdminService;
import com.atguigu.campus.mapper.AdminMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 86175
* @description 针对表【tb_admin】的数据库操作Service实现
* @createDate 2023-05-19 15:43:12
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService{

    @Resource
    private AdminMapper adminMapper;

    //查询账号和密码是否与登录时输入的值相等
    @Override
    public Admin selectAdminByUsernameAndPwd(String username, String password) {
        return adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getName,username).eq(Admin::getPassword,password));
    }

    //查询用户的id(解析token使用到的) 根据不同的id值来分管理员 教师 学生 登录
    @Override
    public Admin selectAdminById(Long userId) {
        return adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getId,userId));
    }
}




