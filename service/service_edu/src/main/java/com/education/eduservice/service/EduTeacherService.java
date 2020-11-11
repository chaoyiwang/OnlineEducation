package com.education.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-08-31
 */
public interface EduTeacherService extends IService<EduTeacher> {
    //1 分页查询讲师的方法
    Map<String, Object> getTeacherList(Page<EduTeacher> pageParam);
}
