package com.education.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.eduservice.entity.frontvo.CourseQueryVo;
import com.education.eduservice.entity.frontvo.CourseWebVo;
import com.education.eduservice.entity.vo.CourseInfoVo;
import com.education.eduservice.entity.vo.CoursePublishVo;
import com.education.eduservice.entity.vo.CourseQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
public interface EduCourseService extends IService<EduCourse> {
    //添加课程基本信息的方法
    String saveCourseInfo(CourseInfoVo courseInfoVo);
    //根据课程id查询课程基本信息
    CourseInfoVo getCourseInfo(String courseId);
    //修改课程基本信息
    void updateCourseInfo(CourseInfoVo courseInfoVo);
    //根据课程id查询课程确认信息
    CoursePublishVo getCoursePublishVoById(String id);
    //课程列表 条件查询带分页
    void pageQuery(Page<EduCourse> pageCourse, CourseQuery courseQuery);
    //删除课程
    void removeCourseById(String courseId);
    //根据讲师id查询这个讲师的课程列表
    List<EduCourse> selectByTeacherId(String id);
    //条件查询带分页查询课程
    Map<String, Object> getCourseList(Page<EduCourse> pageCourse, CourseQueryVo courseQuery);
    //根据课程id，编写sql语句查询课程信息和讲师信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
