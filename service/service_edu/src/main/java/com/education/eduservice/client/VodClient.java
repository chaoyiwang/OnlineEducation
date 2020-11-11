package com.education.eduservice.client;

import com.education.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-vod",fallback = VodFileDegradeFeignClient.class)
@Component
public interface VodClient {
    //定义调用方法路径，直接从vodcontroller复制就行了，改成完整路径，并注入回eduVideoController中
    //PathVariable注解一定要指定参数名称
    //根据视频id删除阿里云视频
    @DeleteMapping("/eduvod/video/removeAliyunVideo/{id}")
    public R removeAliyunVideo(@PathVariable("id") String id);

    //定义调用删除多个视频的方法
    //删除多个阿里云视频的方法
    //参数是多个视频id
    @DeleteMapping("/eduvod/video/deleteBatch")
    public R removeAliyunVideoList(@RequestParam("videoIdList") List<String> videoIdList);
}
