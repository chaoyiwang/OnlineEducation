package com.education.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.eduservice.entity.EduSubject;
import com.education.eduservice.entity.excel.SubjectData;
import com.education.eduservice.service.EduSubjectService;
import com.education.servicebase.exceptionhandler.EducationException;

public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    //因为SubjectExcelListener不能交给Spring进行管理，需要自己new，不能注入其他对象
    //不能实行数据库操作
    public EduSubjectService subjectService;

    public SubjectExcelListener() {
    }

    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if (subjectData == null) {
            throw new EducationException(20001, "添加失败");
        }
        //添加一级分类，判断一级分类是否重复
        EduSubject existFirstClassSubject = this.existFirstClassSubject(subjectService, subjectData.getFirstClassSubjectName());
        if (existFirstClassSubject == null) {//没有相同的一级分类，因此需要添加
            existFirstClassSubject = new EduSubject();
            existFirstClassSubject.setTitle(subjectData.getFirstClassSubjectName());
            existFirstClassSubject.setParentId("0");
            subjectService.save(existFirstClassSubject);
        }

        //获取一级分类id值
        String pid = existFirstClassSubject.getId();

        //添加二级分类
        EduSubject existSecondClassSubject = this.existSecondClassSubject(subjectService, subjectData.getSecondClassSubjectName(), pid);
        if (existSecondClassSubject == null) {
            existSecondClassSubject = new EduSubject();
            existSecondClassSubject.setTitle(subjectData.getSecondClassSubjectName());
            existSecondClassSubject.setParentId(pid);
            subjectService.save(existSecondClassSubject);
        }
    }

    //判断二级分类是否重复
    private EduSubject existSecondClassSubject(EduSubjectService subjectService, String name, String pid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", pid);
        EduSubject secondClassSubject = subjectService.getOne(wrapper);
        return secondClassSubject;
    }

    //判断一级分类是否重复
    private EduSubject existFirstClassSubject(EduSubjectService subjectService, String name) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", "0");
        EduSubject firstClassSubject = subjectService.getOne(wrapper);
        return firstClassSubject;
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
