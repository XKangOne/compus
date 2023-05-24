package com.atguigu.campus.service;

import com.atguigu.campus.pojo.Admin;
import com.atguigu.campus.pojo.Student;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86175
* @description 针对表【tb_student】的数据库操作Service
* @createDate 2023-05-19 15:44:46
*/
public interface StudentService extends IService<Student> {
    Student selectStudentByUsernameAndPwd(String username, String password);

    Student selectStudentById(Long userId);
}
