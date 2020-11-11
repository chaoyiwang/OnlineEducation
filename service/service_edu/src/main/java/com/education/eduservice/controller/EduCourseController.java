package com.education.eduservice.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.commonutils.R;
import com.education.eduservice.entity.EduCourse;
import com.education.eduservice.entity.vo.CourseInfoVo;
import com.education.eduservice.entity.vo.CoursePublishVo;
import com.education.eduservice.entity.vo.CourseQuery;
import com.education.eduservice.service.EduCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
@Api(description = "课程管理")
@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    //课程列表 条件查询带分页
    @ApiOperation(value = "分页课程列表")
    @GetMapping("pagePageCondition/{page}/{limit}")
    public R pageQuery(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
                    CourseQuery courseQuery){

        Page<EduCourse> pageCourse = new Page<>(page, limit);

        courseService.pageQuery(pageCourse, courseQuery);

        List<EduCourse> records = pageCourse.getRecords();
        long total = pageCourse.getTotal();

        return  R.ok().data("total", total).data("records", records);
    }

    //添加课程基本信息的方法
    @ApiOperation(value = "新增课程")
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@ApiParam(name = "CourseInfoForm", value = "课程基本信息", required = true)
                           @RequestBody CourseInfoVo courseInfoVo) {
        //返回添加之后的课程id，为了后面添加大纲使用
        String id = courseService.saveCourseInfo(courseInfoVo);
        return R.ok().data("courseId", id);
    }

    //根据课程id查询课程基本信息
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId) {
        CourseInfoVo courseInfoVo = courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo", courseInfoVo);
    }

    //修改课程基本信息
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo) {
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    //根据课程id查询课程确认信息
    @GetMapping("coursePublishInfo/{id}")
    public R getCoursePublishVoById(@PathVariable String id){
        CoursePublishVo coursePublishVo = courseService.getCoursePublishVoById(id);
        return R.ok().data("publishCourse", coursePublishVo);
    }

    //课程最终发布
    //修改课程状态
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        //设置课程发布状态
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);
        return R.ok();
    }

    //删除课程
    @DeleteMapping("{courseId}")
    public R deleteCourseById(@PathVariable String courseId){
        courseService.removeCourseById(courseId);
        return R.ok();
    }
}

