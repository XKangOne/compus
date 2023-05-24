package com.atguigu.campus.controller;

import com.atguigu.campus.pojo.Grade;
import com.atguigu.campus.service.GradeService;
import com.atguigu.campus.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "年纪控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {

    @Resource
    private GradeService gradeService;

    /**
     * @description 带条件的分页查询功能 模糊查询
     * @param: pn 当前页码
     * @param: pageSize 每页显示的记录数
     * @param: gradeName 模糊查询的条件
     * @return 返回查询结果 Page 对象
    */
    @ApiOperation("带条件的分页查询功能")
    @GetMapping("/getGrades/{pn}/{pageSize}")
    public Result<Object> getGrades(@ApiParam("当前页码") @PathVariable("pn")Integer pn,
                                    @ApiParam("每页显示的记录数")@PathVariable("pageSize")Integer pageSize,
                                    @ApiParam("模糊查询的条件") String gradeName) {
        Page<Grade> page = gradeService.page(new Page<>(pn, pageSize), new LambdaQueryWrapper<Grade>().
                like(StrUtil.isNotBlank(gradeName), Grade::getName, gradeName).orderByDesc(Grade::getId));
        return Result.ok(page);
    }

    /**
     * @description 单条记录和批量删除 功能
     * @param: ids 请求体中的 待删除的年龄id集合
     * @return 返回数据
     */
    @ApiOperation("单条记录和批量删除 功能")
    @DeleteMapping("/deleteGrade")
    public Result<Object> deleteGrade(@ApiParam("ids 请求体中的 待删除的年龄id集合")@RequestBody List<Integer> ids) {
        if (ids.size() == 1) {
            //单条记录的删除
            gradeService.removeById(ids.get(0));
        } else {
            //批量删除
            gradeService.removeBatchByIds(ids);
        }
        return Result.ok();
    }

    /**
     * @description 根据判断请求体中是否有id 进行的添加或修改 功能
     * @param: grade 封装请求体中的JSON数据到 实体类Grade中
     * @return 返回成功数据
    */
    @ApiOperation("根据判断请求体中是否有id 进行的添加或修改 功能")
    @PostMapping("saveOrUpdateGrade")
    public Result<Object> saveOrUpdateGrade(@ApiParam("封装请求体中的JSON数据到 实体类Grade中")
                                                @RequestBody Grade grade) {
        //判断请求体中是否有id 有的话则为修改 否则为添加
        Integer id = grade.getId();
        if (id != null) {
            gradeService.update(grade, new LambdaQueryWrapper<Grade>().eq(Grade::getId, id));
        } else {
            gradeService.save(grade);
        }
        return Result.ok();
    }
    //访问所有年纪
    @GetMapping("/getGrades")
    public Result<Object> getGrades() {
        List<Grade> grades = gradeService.list(null);
        return Result.ok(grades);
    }

}
