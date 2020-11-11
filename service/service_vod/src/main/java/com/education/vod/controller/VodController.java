package com.education.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.education.commonutils.R;
import com.education.vod.service.VodService;
import com.education.vod.utils.AliyunVodSDKUtils;
import com.education.vod.utils.ConstantVodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/eduvod/video")
@CrossOrigin
public class VodController {
    @Autowired
    private VodService vodService;
    //上传视频到阿里云的方法
    @PostMapping("uploadAliyunVideo")
    public R uploadAliyunVideo(MultipartFile file){
        //返回上传视频的id
        String videoId = vodService.uploadAliyunVideo(file);
        return R.ok().data("videoId",videoId);
    }

    //根据视频id删除阿里云视频
    @DeleteMapping("removeAliyunVideo/{id}")
    public R removeAliyunVideo(@PathVariable String id){
        vodService.removeVideoById(id);
        return R.ok().message("视频删除成功");
    }

    //删除多个阿里云视频的方法
    //参数是多个视频id
    @DeleteMapping("deleteBatch")
    public R removeAliyunVideoList(@RequestParam("videoIdList") List<String> videoIdList){
        vodService.removeAliyunVideoList(videoIdList);
        return R.ok().message("视频删除成功");
    }

    //根据视频id获取视频凭证
    @GetMapping("getPlayAuth/{videoId}")
    public R getVideoPlayAuth(@PathVariable("videoId") String videoId) throws Exception {

        //获取阿里云存储相关常量
        String accessKeyId = ConstantVodUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantVodUtil.ACCESS_KEY_SECRET;

        //创建初始化对象
        DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(accessKeyId, accessKeySecret);

        //创建获取凭证request和response对象
        //请求
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        //向request设置视频id
        request.setVideoId(videoId);
        //响应
        GetVideoPlayAuthResponse response = client.getAcsResponse(request);
        //调用方法得到播放凭证
        String playAuth = response.getPlayAuth();

        //返回结果
        return R.ok().message("获取凭证成功").data("playAuth", playAuth);
    }
}
