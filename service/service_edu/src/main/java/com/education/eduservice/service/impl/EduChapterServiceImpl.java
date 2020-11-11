package com.education.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.eduservice.entity.EduChapter;
import com.education.eduservice.entity.EduVideo;
import com.education.eduservice.entity.chapter.ChapterVo;
import com.education.eduservice.entity.chapter.VideoVo;
import com.education.eduservice.mapper.EduChapterMapper;
import com.education.eduservice.service.EduChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.eduservice.service.EduVideoService;
import com.education.servicebase.exceptionhandler.EducationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService videoService;

    //返回课程大纲列表，根据course id进行查询
    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {
        //1 根据课程id，查询里面所有章节
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id", courseId);
        //返回所有章节的集合
        List<EduChapter> eduChapterList = baseMapper.selectList(wrapperChapter);

        //2  根据课程id，查询里面所有小节
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id", courseId);
        //返回所有小节的集合
        List<EduVideo> eduVideoList = videoService.list(wrapperVideo);

        //创建一个list集合，用于最终封装数据
        List<ChapterVo> finalList = new ArrayList<>();

        //3 遍历查询章节list集合进行封装
        //遍历查询章节list集合
        for (int i = 0; i < eduChapterList.size(); i++) {
            //每个章节
            EduChapter eduChapter = eduChapterList.get(i);
            //把eduChapter里面的值复制到ChapterVo里
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(eduChapter, chapterVo);
            //把chapterVo放到最终list里面去
            finalList.add(chapterVo);
            //创建集合用于封装章节里的小节
            List<VideoVo> videoList = new ArrayList<>();
            //4 遍历查询小节list集合进行封装
            for (int j = 0; j < eduVideoList.size(); j++) {
                //得到每个小节
                EduVideo eduVideo = eduVideoList.get(j);
                //判断小节里的chapter id和章节里的id是否一样，如果是才封装
                if (eduVideo.getChapterId().equals(eduChapter.getId())) {
                    //进行封装
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo, videoVo);
                    //放到小节的封装集合中去
                    videoList.add(videoVo);
                }
            }
            //把封装之后的小节List集合，放到章节的list中去
            chapterVo.setChildren(videoList);
        }
        return finalList;
    }

    //删除章节的方法
    @Override
    public boolean deleteChapter(String chapterId) {
        //根据chapterid查询小节表，如果能查询出数据，说明chapter下还有小节，那么不进行删除
        QueryWrapper<EduVideo> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("chapter_id", chapterId);
        //按照条件能查出几条记录：edu_video表中，chapter_id为**的有多少条记录
        int count = videoService.count(videoQueryWrapper);
        //判断如果count如果大于0，表示里面有小节，不进行删除
        if (count > 0) {
            throw new EducationException(20001, "Chapter内仍有小节，无法进行删除！");
        } else {//判断如果count如果等于0，表示里面没有小节，进行删除
            int result = baseMapper.deleteById(chapterId);
            //如果删除成功，result = 1，返回true
            return result > 0;
        }
    }

    //根据id删除所有章节
    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        baseMapper.delete(queryWrapper);
    }
}
