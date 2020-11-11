package com.education.eduservice.service;

import com.education.eduservice.entity.EduChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.eduservice.entity.chapter.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
public interface EduChapterService extends IService<EduChapter> {
    //返回课程大纲列表，根据course id进行查询
    List<ChapterVo> getChapterVideoByCourseId(String courseId);
    //删除的方法
    boolean deleteChapter(String chapterId);
    //根据id删除所有章节
    void removeChapterByCourseId(String courseId);
}
