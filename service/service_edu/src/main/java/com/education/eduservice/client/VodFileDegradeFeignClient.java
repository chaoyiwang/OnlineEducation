package com.education.eduservice.client;

import com.education.commonutils.R;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class VodFileDegradeFeignClient implements VodClient {
    //出错熔断之后才会实行的方法
    @Override
    public R removeAliyunVideo(String id) {
        return R.error().message("time out");
    }

    @Override
    public R removeAliyunVideoList(List<String> videoIdList) {
        return R.error().message("time out");
    }
}
