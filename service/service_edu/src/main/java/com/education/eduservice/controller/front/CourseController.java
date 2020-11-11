package com.education.eduservice.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.commonutils.CourseWebVoOrder;
import com.education.commonutils.JwtUtils;
import com.education.commonutils.R;
import com.education.eduservice.client.OrderClient;
import com.education.eduservice.entity.EduCourse;
import com.education.eduservice.entity.chapter.ChapterVo;
import com.education.eduservice.entity.frontvo.CourseQueryVo;
import com.education.eduservice.entity.frontvo.CourseWebVo;
import com.education.eduservice.service.EduChapterService;
import com.education.eduservice.service.EduCourseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class CourseController {
    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OrderClient orderClient;

    //1 条件查询带分页查询课程
    @ApiOperation(value = "分页课程列表")
    @PostMapping("getCourseList/{page}/{limit}")
    public R pageList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
            @RequestBody(required = false) CourseQueryVo courseQuery){
        Page<EduCourse> pageCourse = new Page<EduCourse>(page, limit);
        Map<String, Object> map = courseService.getCourseList(pageCourse, courseQuery);
        return  R.ok().data(map);
    }
    // 2 课程详情的方法
    @ApiOperation(value = "根据ID查询课程")
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfoById(
            @ApiParam(name = "courseId", value = "课程ID", required = true)
            @PathVariable String courseId, HttpServletRequest request){

        //根据课程id，编写sql语句查询课程信息和讲师信息
        CourseWebVo courseWebVo = courseService.getBaseCourseInfo(courseId);
        //根据课程id，查询当前课程的章节信息
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);
        //根据用户id和课程id查询当前课程是否已经被支付过了
        boolean buyCourse = orderClient.isBuyCourse(JwtUtils.getMemberIdByJwtToken(request), courseId);

        return R.ok().data("course", courseWebVo).data("chapterVideoList", chapterVideoList).data("isbuy",buyCourse);
    }

    //根据课程id查询课程信息
    @GetMapping("getDto/{courseId}")
    public CourseWebVoOrder getCourseInfoDto(@PathVariable String courseId) {
        CourseWebVo courseInfoForm = courseService.getBaseCourseInfo(courseId);
        CourseWebVoOrder courseInfo = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfoForm,courseInfo);
        return courseInfo;
    }
}
