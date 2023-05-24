package com.atguigu.campus.service;

import com.atguigu.campus.pojo.Admin;
import com.atguigu.campus.pojo.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86175
* @description 针对表【tb_teacher】的数据库操作Service
* @createDate 2023-05-19 15:44:50
*/
public interface TeacherService extends IService<Teacher> {
    Teacher selectTeacherByUsernameAndPwd(String username, String password);

    Teacher selectTeacherById(Long userId);
}
