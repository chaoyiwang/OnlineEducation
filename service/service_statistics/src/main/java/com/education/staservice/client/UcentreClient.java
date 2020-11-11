package com.education.staservice.client;

import com.education.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-ucenter")
public interface UcentreClient {
    @GetMapping(value = "/educenter/member/countregister/{day}")
    public R registerCount(@PathVariable("day") String day);
}
