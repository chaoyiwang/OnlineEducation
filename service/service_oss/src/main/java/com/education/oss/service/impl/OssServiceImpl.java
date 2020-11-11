package com.education.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.education.oss.service.OssService;
import com.education.oss.utils.ConstantPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    //上传头像到oss
    @Override
    public String uploadFileAvatar(MultipartFile file) {

        //根据工具类获取阿里云存储相关常量
        String endPoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
            // 获取上传文件的输入流
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            String fileName = file.getOriginalFilename();
            //在文件名称里添加UUID,去除里面的-
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String datePath = new DateTime().toString("yyyyMMdd");
            fileName = uuid+datePath+fileName;
            //调用oss方法实现上传，第二个参数是上传到oss文件路径和文件名称/aa/bb/1.jpg
            ossClient.putObject(bucketName, fileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //把上传之后的路径返回，需要把上传到阿里云oss路径手动拼接
            String url = "http://" + bucketName + "." + endPoint + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
