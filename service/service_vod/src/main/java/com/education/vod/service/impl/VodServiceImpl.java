package com.education.vod.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.education.servicebase.exceptionhandler.EducationException;
import com.education.vod.service.VodService;
import com.education.vod.utils.AliyunVodSDKUtils;
import com.education.vod.utils.ConstantVodUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class VodServiceImpl implements VodService {

    //上传视频到阿里云的方法
    @Override
    public String uploadAliyunVideo(MultipartFile file) {
        try {

            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            //上传后显示名称，去掉后缀
            String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));

            UploadStreamRequest request = new UploadStreamRequest(
                    ConstantVodUtil.ACCESS_KEY_ID,
                    ConstantVodUtil.ACCESS_KEY_SECRET,
                    title, originalFilename, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            String videoId = response.getVideoId();
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                if (StringUtils.isEmpty(videoId)) {
                    throw new EducationException(20001, errorMessage);
                }
            }

            return videoId;
        } catch (IOException e) {
            throw new EducationException(20001, "Vod 服务上传失败");
        }
    }

    //根据视频id删除阿里云视频
    @Override
    public void removeVideoById(String id) {
        try {
            //初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantVodUtil.ACCESS_KEY_ID,
                    ConstantVodUtil.ACCESS_KEY_SECRET);
            //创建删除视频request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //向request设置视频id
            request.setVideoIds(id);
            //调用初始化对象的方法实现删除
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.print("RequestId = " + response.getRequestId() + "\n");
        } catch (Exception e) {
            throw new EducationException(20001, "视频删除失败");
        }
    }
    //删除多个阿里云视频的方法
    @Override
    public void removeAliyunVideoList(List videoIdList) {
        try {
            //初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantVodUtil.ACCESS_KEY_ID,
                    ConstantVodUtil.ACCESS_KEY_SECRET);
            //创建删除视频request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //把videoList中的值转换成逗号分隔
            String videoIds = org.apache.commons.lang.StringUtils.join(videoIdList.toArray(), ",");
            //向request设置视频id
            request.setVideoIds(videoIds);
            //调用初始化对象的方法实现删除
            client.getAcsResponse(request);
        } catch (Exception e) {
            throw new EducationException(20001, "视频删除失败");
        }
    }
}
