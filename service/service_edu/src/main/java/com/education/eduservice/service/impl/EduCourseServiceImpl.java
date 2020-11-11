package com.education.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.eduservice.entity.EduCourse;
import com.education.eduservice.entity.EduCourseDescription;
import com.education.eduservice.entity.frontvo.CourseQueryVo;
import com.education.eduservice.entity.frontvo.CourseWebVo;
import com.education.eduservice.entity.vo.CourseInfoVo;
import com.education.eduservice.entity.vo.CoursePublishVo;
import com.education.eduservice.entity.vo.CourseQuery;
import com.education.eduservice.mapper.EduCourseMapper;
import com.education.eduservice.service.EduChapterService;
import com.education.eduservice.service.EduCourseDescriptionService;
import com.education.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.eduservice.service.EduVideoService;
import com.education.servicebase.exceptionhandler.EducationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    //注入课程描述
    @Autowired
    private EduCourseDescriptionService courseDescriptionService;
    //注入课程章节和小节
    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private EduVideoService videoService;

    //添加课程基本信息的方法
    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {
        //把CourseInfoVo转换成eduCourse
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        //1 向课程表添加课程基本信息，返回成功加了几条记录
        int insert = baseMapper.insert(eduCourse);
        if (insert == 0) {
            //添加失败
            throw new EducationException(20001, "添加课程信息失败！");
        }
        //获取添加之后的课程id
        String cid = eduCourse.getId();
        //2 向课程简介表添加课程简介
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
        //设置描述id就是课程id
        courseDescription.setId(cid);
        courseDescriptionService.save(courseDescription);
        return cid;
    }

    //根据课程id查询课程基本信息
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        //1 查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        //封装到CourseInfoVo中
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse, courseInfoVo);
        //2 查询课程描述表
        EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
        //继续封装到CourseInfoVo中，因为只有一个
        courseInfoVo.setDescription(courseDescription.getDescription());
        return courseInfoVo;
    }

    //修改课程基本信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        //courseInfoVo变EduCourse
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo, eduCourse);
        //1 修改课程基本信息表
        //影响的行数
        int update = baseMapper.updateById(eduCourse);
        if (update == 0) {//修改失败
            throw new EducationException(20001, "修改课程信息失败！");
        }
        //2 修改课程描述表
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfoVo.getId());
        courseDescription.setDescription(courseInfoVo.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        //调用自己写的mapper，必须使用baseMapper
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfoById(id);
        return publishCourseInfo;
    }

    //课程列表 条件查询带分页
    @Override
    public void pageQuery(Page<EduCourse> pageCourse, CourseQuery courseQuery) {
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("gmt_create");

        if (courseQuery == null) {
            baseMapper.selectPage(pageCourse, queryWrapper);
            return;
        }

        String title = courseQuery.getTitle();
        String teacherId = courseQuery.getTeacherId();
        String subjectParentId = courseQuery.getSubjectParentId();
        String subjectId = courseQuery.getSubjectId();

        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }

        if (!StringUtils.isEmpty(teacherId)) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.ge("subject_parent_id", subjectParentId);
        }

        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.ge("subject_id", subjectId);
        }

        baseMapper.selectPage(pageCourse, queryWrapper);
    }

    //删除课程
    @Override
    public void removeCourseById(String courseId) {
        //根据id删除所有视频
        videoService.removeVideoByCourseId(courseId);
        //根据id删除所有章节
        chapterService.removeChapterByCourseId(courseId);
        //根据id删除课程描述
        courseDescriptionService.removeById(courseId);
        //根据id删除课程本身
        int result = baseMapper.deleteById(courseId);
        if (result ==0){
            throw new EducationException(20001, "删除失败！");}
    }
    //根据讲师id查询这个讲师的课程列表
    @Override
    public List<EduCourse> selectByTeacherId(String id) {
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<EduCourse>();
        queryWrapper.eq("teacher_id", id);
        //按照最后更新时间倒序排列
        queryWrapper.orderByDesc("gmt_modified");
        List<EduCourse> courses = baseMapper.selectList(queryWrapper);
        return courses;
    }
    //条件查询带分页查询课程
    @Override
    public Map<String, Object> getCourseList(Page<EduCourse> pageCourse, CourseQueryVo courseQuery) {
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        //判断条件是否为空，如果不为空才拼接
        //一级分类
        if (!StringUtils.isEmpty(courseQuery.getSubjectParentId())) {
            queryWrapper.eq("subject_parent_id", courseQuery.getSubjectParentId());
        }
        //二级分类
        if (!StringUtils.isEmpty(courseQuery.getSubjectId())) {
            queryWrapper.eq("subject_id", courseQuery.getSubjectId());
        }
        // 销量、关注度
        if (!StringUtils.isEmpty(courseQuery.getBuyCountSort())) {
            queryWrapper.orderByDesc("buy_count");
        }
        // 最新
        if (!StringUtils.isEmpty(courseQuery.getGmtCreateSort())) {
            queryWrapper.orderByDesc("gmt_create");
        }
        // 价格
        if (!StringUtils.isEmpty(courseQuery.getPriceSort())) {
            queryWrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageCourse, queryWrapper);

        List<EduCourse> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }
    //根据课程id，编写sql语句查询课程信息和讲师信息
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }
}
