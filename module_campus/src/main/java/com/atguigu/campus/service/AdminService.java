package com.atguigu.campus.service;

import com.atguigu.campus.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86175
* @description 针对表【tb_admin】的数据库操作Service
* @createDate 2023-05-19 15:43:12
*/
public interface AdminService extends IService<Admin> {

    Admin selectAdminByUsernameAndPwd(String username, String password);

    Admin selectAdminById(Long userId);
}
