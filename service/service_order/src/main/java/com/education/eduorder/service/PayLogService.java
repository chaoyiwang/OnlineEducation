package com.education.eduorder.service;

import com.education.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-10-03
 */
public interface PayLogService extends IService<PayLog> {
    //生成微信支付二维码
    Map createNative(String orderNo);
    //查询订单支付状态
    Map<String, String> queryPayStatus(String orderNo);
    //更改订单状态
    void updateOrderStatus(Map<String, String> map);
}
