package com.education.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.eduservice.entity.EduSubject;
import com.education.eduservice.entity.excel.SubjectData;
import com.education.eduservice.entity.subject.FirstClassSubject;
import com.education.eduservice.entity.subject.SecondClassSubject;
import com.education.eduservice.listener.SubjectExcelListener;
import com.education.eduservice.mapper.EduSubjectMapper;
import com.education.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.servicebase.exceptionhandler.EducationException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-04
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {
    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            //1 获取文件输入流
            InputStream inputStream = file.getInputStream();
            // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
            EasyExcel.read(inputStream, SubjectData.class, new SubjectExcelListener(subjectService)).sheet().doRead();
        } catch (Exception e) {
            e.printStackTrace();
            throw new EducationException(20002, "添加课程分类失败");
        }
    }

    //课程分类列表（树形）
    @Override
    public List<FirstClassSubject> getAllClassSubjects() {
        //1.查询所有一级分类parent_id = 0
        QueryWrapper<EduSubject> queryFirstClassWrapper = new QueryWrapper<>();
        queryFirstClassWrapper.eq("parent_id", 0);
        queryFirstClassWrapper.orderByAsc("sort", "id");
        List<EduSubject> firstSubjectList = baseMapper.selectList(queryFirstClassWrapper);
        //2.查询所有二级分类parent_id != 0
        QueryWrapper<EduSubject> querySecondClassWrapper = new QueryWrapper<>();
        querySecondClassWrapper.ne("parent_id", 0);
        querySecondClassWrapper.orderByAsc("sort", "id");
        List<EduSubject> secondSubjectList = baseMapper.selectList(querySecondClassWrapper);

        //创建一个List集合用于存储最终封装的数据
        List<FirstClassSubject> finalSubjectList = new ArrayList<>();
        //3.封装一级分类
        for (int i = 0; i < firstSubjectList.size(); i++) {//遍历firstSubjectList集合
            //得到firstSubjectList每个eduSubject对象
            EduSubject subject = firstSubjectList.get(i);
            //把eduSubject里面放值获取出来，放到FirstClassSubject对象中去
            FirstClassSubject firstClassSubject = new FirstClassSubject();
            BeanUtils.copyProperties(subject, firstClassSubject);
            //多个FirstClassSubject放到finalSubjectList中
            finalSubjectList.add(firstClassSubject);

            //4.封装二级分类,，在一级分类循环内遍历查询所有的二级分类
            //创建List集合封装每个一级分类的二级分类
            List<SecondClassSubject> secondFinalSubjectList = new ArrayList<>();
            //遍历二级分类list集合
            for (int j = 0; j < secondSubjectList.size(); j++) {
                //获取每个二级分类
                EduSubject subSubject = secondSubjectList.get(j);
                //判断二级分类parent_id和一级分类id是否一样
                if (subject.getId().equals(subSubject.getParentId())) {
                    //创建二级类别vo对象
                    SecondClassSubject secondClassSubject = new SecondClassSubject();
                    BeanUtils.copyProperties(subSubject, secondClassSubject);
                    //多个SecondClassSubject放到finalSubjectList中
                    secondFinalSubjectList.add(secondClassSubject);
                }
            }
            firstClassSubject.setChildren(secondFinalSubjectList);
        }
        return finalSubjectList;
    }
}
