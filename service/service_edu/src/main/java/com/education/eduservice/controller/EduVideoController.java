package com.education.eduservice.controller;


import com.education.commonutils.R;
import com.education.eduservice.client.VodClient;
import com.education.eduservice.entity.EduVideo;
import com.education.eduservice.service.EduVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-09-06
 */
@RestController
@RequestMapping("/eduservice/video")
@CrossOrigin
public class EduVideoController {
    @Autowired
    private EduVideoService videoService;
    @Autowired
    private VodClient vodClient;

    //添加小节
    @PostMapping("addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo) {
        videoService.save(eduVideo);
        return R.ok();
    }

    //删除小节
    @DeleteMapping("{id}")
    public R deleteVideoById(@PathVariable String id) {
        //1、根据小节id获取视频id，调用方法实现视频删除
        EduVideo video = videoService.getById(id);
        String videoSourceId = video.getVideoSourceId();
        //根据视频id，远程调用实现视频删除
        //删除视频资源
        if (!StringUtils.isEmpty(videoSourceId)) {
            vodClient.removeAliyunVideo(videoSourceId);
        }
        //2、删除小节
        boolean result = videoService.removeById(id);
        if (result) {
            return R.ok();
        } else {
            return R.error().message("删除失败");
        }
    }

    //根据ID查询小节
    @GetMapping("videoInfo/{id}")
    public R getVideInfoById(@PathVariable String id) {
        EduVideo eduVideo = videoService.getById(id);
        return R.ok().data("video", eduVideo);
    }

    //修改小节
    @PutMapping("updateVideoInfo/{id}")
    public R updateVideoInfoById(@RequestBody EduVideo eduVideo, @PathVariable String id) {
        videoService.updateById(eduVideo);
        return R.ok();
    }
}

