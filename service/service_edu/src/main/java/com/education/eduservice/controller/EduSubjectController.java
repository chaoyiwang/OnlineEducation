package com.education.eduservice.controller;


import com.education.commonutils.R;
import com.education.eduservice.entity.subject.FirstClassSubject;
import com.education.eduservice.service.EduSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-09-04
 */
@RestController
@RequestMapping("/eduservice/subject")
@CrossOrigin
public class EduSubjectController {

    @Autowired
    private EduSubjectService subjectService;

    //添加课程分类
    //获取上传到oss中的excel文件，把文件内容读取出来
    @PostMapping("importSubject")
    public R addSubject(MultipartFile file) {
        //上传excel文件
        subjectService.saveSubject(file, subjectService);
        return R.ok();
    }

    //课程分类列表（树形）
    @GetMapping("getAllSubjects")
    public R getAllSubjects() {
        //List集合泛型是一级分类
        List<FirstClassSubject> list = subjectService.getAllClassSubjects();
        return R.ok().data("list", list);
    }
}

