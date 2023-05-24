package com.atguigu.campus.controller;

import com.atguigu.campus.pojo.Admin;
import com.atguigu.campus.pojo.LoginForm;
import com.atguigu.campus.pojo.Student;
import com.atguigu.campus.pojo.Teacher;
import com.atguigu.campus.service.AdminService;
import com.atguigu.campus.service.StudentService;
import com.atguigu.campus.service.TeacherService;
import com.atguigu.campus.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/***
 * @description: 将封装在result类中的数据以json格式返回给前端浏览器
 * @author: yk
 **/
@Api(tags = "系统控制层")
@RestController
@RequestMapping("/sms/system")
public class SystemController {
    @Resource
    private AdminService adminService;

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;
    /**
     * 获取验证码图片响应到浏览器,并将验证码中的值保存到session域中 用于用户登录时/login校验
     */
    @ApiOperation("获取验证码图片")
    @RequestMapping ("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpSession session, HttpServletResponse response) throws IOException {
        //通过工具类CreateVerifiCodeImage 获得验证码图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取验证码图片中的值 并保存在session域中 用于用于登录时校验
        String code = new String(CreateVerifiCodeImage.getVerifiCode());
        session.setAttribute("code", code);
        //将获取到的验证码图片响应到浏览器
        ImageIO.write(verifiCodeImage, "JPG", response.getOutputStream());
    }

    /**
     * 登录:进行验证码以及用户输入的账号密码进行校验
     * @return 将校验的结果数据封装到Result类中返回给浏览器 若用户登录成功 则根据id和用户类型生成一个token放在Result类一起返回给浏览器
     */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginForm loginForm, HttpSession session) {
        //先获取session域中的验证码值
        String code = (String) session.getAttribute("code");
        //判断是否失效
        if (code == null || "".equals(code)) {
            return Result.fail().message("验证码失效,请重新输入验证码");
        }
        //获取用户输入的验证码
        String userInputCode = loginForm.getVerifiCode();
        //equalsIgnoreCase忽略大小写
        //判断用户输入验证码与实际验证码的值是否相等
        if (!userInputCode.equalsIgnoreCase(code)) {
            return Result.fail().message("验证码输入有误");
        }
        //销毁session 域中的验证码的值
        session.removeAttribute("code");
        //获取用户类型 根据用户类型去相应的表中，做用户名和密码的校验
        Integer userType = loginForm.getUserType();
        //获取用户输入的用户名和密码
        String username = loginForm.getUsername();
        //对密码进行加密
        String password = MD5.encrypt(loginForm.getPassword());
        Map<String, Object> map = new LinkedHashMap<>();
        if (userType == 1) {
            Admin admin = adminService.selectAdminByUsernameAndPwd(username, password);
            //判断输入的用户名和密码是否存在于数据库中
            //若查询结果为null 则数据库中查无相应的账号和密码 登录失败
            if (admin != null) {
                //登录成功后 需要根据用户id和用户类型生成token 并返回给浏览器
                //让浏览器通过token再发送请求来进行解析 告诉前端应该前往哪个用户以及哪个类型用户的首页
                String token = JwtHelper.createToken(admin.getId().longValue(), userType);
                map.put("token", token);
//                //需要将用户类型也返回给浏览器
//                map.put("userType", userType);
                return Result.ok(map);
            }
            return Result.fail().message("用户名或者密码有误");
        } else if (userType == 2) {
            Student student = studentService.selectStudentByUsernameAndPwd(username, password);
            if (student != null) {
                //登录成功后 需要根据用户id和用户类型生成token 并返回给浏览器
                String token = JwtHelper.createToken(student.getId().longValue(), userType);
                map.put("token", token);
//                //需要将用户类型也返回给浏览器
//                map.put("userType", userType);
                return Result.ok(map);
            }
            return Result.fail().message("用户名或者密码有误");
        } else {
            Teacher teacher = teacherService.selectTeacherByUsernameAndPwd(username, password);
            if (teacher!= null) {
                //登录成功后 需要根据用户id和用户类型生成token 并返回给浏览器
                String token = JwtHelper.createToken(teacher.getId().longValue(), userType);
                map.put("token", token);
//                //需要将用户类型也返回给浏览器
//                map.put("userType", userType);
                return Result.ok(map);
            }
            return Result.fail().message("用户名或者密码有误");
        }
    }

    /**
     * 解析 浏览器发送来的请求头中的token
     *
     * @param token 浏览器发送来的token
     * @return 封装数据到Result类 响应给浏览器
     */
    @GetMapping("/getInfo")
    public Result<Object> getInfo(@RequestHeader("token") String token) {
        //先判断token是否有效
        if (JwtHelper.isExpiration(token)) {
            //失效
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //没有失效，解析token 获取用户id和用户类型 将用户类型返回给浏览器
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userType", userType);
        if (userType == 1) {
           Admin admin =  adminService.selectAdminById(userId);
            map.put("user", admin);
        } else if (userType == 2) {
            Student student = studentService.selectStudentById(userId);
            map.put("user", student);
        }else {
            Teacher teacher = teacherService.selectTeacherById(userId);
            map.put("user", teacher);
        }
        return Result.ok(map);
    }

    @ApiOperation("上传头像")
    @PostMapping("/headerImgUpload")
    public Result<Object> headerImgUpload(@ApiParam("封装请求体中的图片二进制数据") @RequestPart("multipartFile") MultipartFile multipartFile) throws IOException {
        //先获取上传文件的名称
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        //获取文件格式 即后缀 如 .jpg .png .jepg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //为了避免文件保存到服务端时 文件名相同的冲突 导致文件覆盖 所有使用UUID随机生成数
        String fileName = UUID.randomUUID().toString().toLowerCase().replace("-", "").concat(suffix);
//        String photoName = UUID.randomUUID().toString().replace("-", "").toLowerCase().
//                concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        //保存路径
        String savePath = "E:\\IDEA\\campus\\module_campus\\src\\main\\resources\\static\\upload\\".concat(fileName);
        //保存图片
        multipartFile.transferTo(new File(savePath));
        return Result.ok("upload/".concat(fileName));
    }

    @ApiOperation("修改用户密码功能")
    @PostMapping("updatePwd/{oldPwd}/{newPwd}")
    public Result<Object> updatePwd(@ApiParam("请求头中的token数据") @RequestHeader("token") String token,
                                    @ApiParam("路径参数中的原密码") @PathVariable("oldPwd") String oldPwd,
                                    @ApiParam("路径参数中的新密码") @PathVariable("newPwd") String newPwd) {
        //先判断一下token是否过期
        if (JwtHelper.isExpiration(token)) {
            return Result.fail().message("token失效,请重新登录后修改");
        }
        //获取用户id 根据id 查询用户原密码是否输入正确
        Long userId = JwtHelper.getUserId(token);
        //根据token 判断用户类型
        Integer userType = JwtHelper.getUserType(token);
        assert userType != null;
        if(userType == 1){
            //对旧密码进行校验
            Admin admin = adminService.selectAdminById(userId);
            if(!MD5.encrypt(oldPwd).equals(admin.getPassword())){
                return Result.fail().message("原密码输入有误");
            }
            //原密码输入正确 就将新密码进行加密后进行修改
            admin.setPassword(MD5.encrypt(newPwd));
            adminService.update(admin,new LambdaQueryWrapper<Admin>().eq(Admin::getId,userId));
        }else if(userType == 2){
            Student student = studentService.selectStudentById(userId);
            if(!MD5.encrypt(oldPwd).equals(student.getPassword())){
                return Result.fail().message("原密码输入有误，请重新输入！");
            }
            student.setPassword(MD5.encrypt(newPwd));
            studentService.update(student,new LambdaQueryWrapper<Student>().eq(Student::getId,userId));
        }else {
            Teacher teacher = teacherService.selectTeacherById(userId);
            if(!MD5.encrypt(oldPwd).equals(teacher.getPassword())){
                return Result.fail().message("原密码输入有误");
            }
            teacher.setPassword(MD5.encrypt(newPwd));
            teacherService.update(teacher,new LambdaQueryWrapper<Teacher>().eq(Teacher::getId,userId));
        }
        return Result.ok();
    }

}
