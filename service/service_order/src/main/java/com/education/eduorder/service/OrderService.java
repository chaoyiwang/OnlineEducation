package com.education.eduorder.service;

import com.education.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-10-03
 */
public interface OrderService extends IService<Order> {
    //根据课程id和用户id创建订单，返回订单id
    String saveOrder(String courseId, String memberIdByJwtToken);
}
