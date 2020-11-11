package com.education.eduorder.client;

import com.education.commonutils.CourseWebVoOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-edu")
public interface EduClient {
    //根据课程id查询课程信息
    @GetMapping("/eduservice/course/getDto/{courseId}")
    public CourseWebVoOrder getCourseInfoDto(@PathVariable("courseId") String courseId);
}
