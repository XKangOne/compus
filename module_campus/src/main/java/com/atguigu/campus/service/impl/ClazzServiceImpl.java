package com.atguigu.campus.service.impl;

import com.atguigu.campus.mapper.AdminMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.campus.pojo.Clazz;
import com.atguigu.campus.service.ClazzService;
import com.atguigu.campus.mapper.ClazzMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 86175
* @description 针对表【tb_clazz】的数据库操作Service实现
* @createDate 2023-05-19 15:44:36
*/
@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz>
    implements ClazzService{

}




