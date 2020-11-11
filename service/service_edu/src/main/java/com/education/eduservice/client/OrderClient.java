package com.education.eduservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "service-order")
public interface OrderClient {
    //根据用户id和课程id查询订单信息
    @GetMapping("/eduorder/order/isBuyCourse/{memberid}/{courseid}")
    public boolean isBuyCourse(@PathVariable("memberid") String memberid,
                               @PathVariable("courseid") String courseid);
}
