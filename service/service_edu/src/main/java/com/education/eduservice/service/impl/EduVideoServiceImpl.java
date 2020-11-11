package com.education.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.eduservice.client.VodClient;
import com.education.eduservice.entity.EduVideo;
import com.education.eduservice.mapper.EduVideoMapper;
import com.education.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {
    //注入vodClient
    @Autowired
    private VodClient vodClient;

    //根据id删除所有视频
    @Override
    public void removeVideoByCourseId(String courseId) {
        //1、根据课程id查询课程所有的视频id
        //根据课程id查询所有视频列表
        QueryWrapper<EduVideo> videoWrapper = new QueryWrapper<>();
        videoWrapper.eq("course_id", courseId);
        videoWrapper.select("video_source_id");
        List<EduVideo> videoList = baseMapper.selectList(videoWrapper);

        //得到所有视频列表的云端原始视频id
        //将List<EduVideo>变成List<String>
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < videoList.size(); i++) {
            EduVideo eduVideo = videoList.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();
            //放到videoIds集合中去
            if (!StringUtils.isEmpty(videoSourceId)) {
                videoIds.add(videoSourceId);
            }
        }
        //调用vod服务删除远程视频，根据多个视频id删除多个视频
        if (videoIds.size() > 0) {
            vodClient.removeAliyunVideoList(videoIds);
        }
        //2、删除小节
        QueryWrapper<EduVideo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        baseMapper.delete(queryWrapper);
    }
}
