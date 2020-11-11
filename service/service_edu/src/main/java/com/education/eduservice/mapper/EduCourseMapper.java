package com.education.eduservice.mapper;

import com.education.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.education.eduservice.entity.frontvo.CourseWebVo;
import com.education.eduservice.entity.vo.CoursePublishVo;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    public CoursePublishVo getPublishCourseInfoById(String courseId);
    //根据课程id，编写sql语句查询课程信息和讲师信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
