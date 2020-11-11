package com.education.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.commonutils.JwtUtils;
import com.education.commonutils.R;
import com.education.eduorder.entity.Order;
import com.education.eduorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-10-03
 */
@RestController
@RequestMapping("/eduorder/order")
@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;
    //根据课程id和用户id创建订单，返回订单id
    @PostMapping("createOrder/{courseId}")
    public R save(@PathVariable String courseId, HttpServletRequest request) {
        String orderId = orderService.saveOrder(courseId, JwtUtils.getMemberIdByJwtToken(request));
        return R.ok().data("orderId", orderId);
    }
    //根据订单id获取订单信息接口
    @GetMapping("getOrder/{orderId}")
    public R get(@PathVariable String orderId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderId);
        Order order = orderService.getOne(wrapper);
        return R.ok().data("item", order);
    }
    //根据用户id和课程id查询订单信息
    @GetMapping("isBuyCourse/{memberid}/{courseid}")
    public boolean isBuyCourse(@PathVariable String memberid,
                               @PathVariable String courseid) {
        //订单状态是1表示支付成功
        int count = orderService.count(new QueryWrapper<Order>().eq("member_id", memberid).eq("course_id", courseid).eq("status", 1));
        if(count>0) {//表示有记录，说明已经支付
            return true;
        } else {
            return false;
        }
    }
}

