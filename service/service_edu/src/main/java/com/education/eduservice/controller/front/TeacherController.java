package com.education.eduservice.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.commonutils.R;
import com.education.eduservice.entity.EduCourse;
import com.education.eduservice.entity.EduTeacher;
import com.education.eduservice.service.EduCourseService;
import com.education.eduservice.service.EduTeacherService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduservice/teacher")
@CrossOrigin
public class TeacherController {
    //把service注入
    @Autowired
    private EduTeacherService teacherService;
    @Autowired
    private EduCourseService courseService;

    //1 分页查询讲师的方法
    @ApiOperation(value = "分页讲师列表")
    @GetMapping("getTeacherList/{page}/{limit}")
    public R getTeacherList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit){

        Page<EduTeacher> pageParam = new Page<EduTeacher>(page, limit);
        Map<String, Object> map = teacherService.getTeacherList(pageParam);
        //通过map返回分页所有数据
        return  R.ok().data(map);
    }

    //2 讲师详情的方法
    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping(value = "getTeacherInfo/{id}")
    public R getTeacherInfo(
            @ApiParam(name = "id", value = "讲师ID", required = true)
            @PathVariable String id){

        //根据讲师id查询这个讲师的信息
        EduTeacher teacher = teacherService.getById(id);
        //根据讲师id查询这个讲师的课程列表
        List<EduCourse> courseList = courseService.selectByTeacherId(id);
        return R.ok().data("teacher", teacher).data("courseList", courseList);
    }
}
