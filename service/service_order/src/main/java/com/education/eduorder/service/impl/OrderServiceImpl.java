package com.education.eduorder.service.impl;

import com.education.commonutils.CourseWebVoOrder;
import com.education.commonutils.UcenterMemberOrder;
import com.education.eduorder.client.EduClient;
import com.education.eduorder.client.UcenterClient;
import com.education.eduorder.entity.Order;
import com.education.eduorder.mapper.OrderMapper;
import com.education.eduorder.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.eduorder.utils.OrderNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-10-03
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;

    //1 生成订单的方法 根据课程id和用户id创建订单，返回订单id
    @Override
    public String saveOrder(String courseId, String memberIdByJwtToken) {
        //通过远程调用根据课程id获取课信息
        CourseWebVoOrder courseInfo = eduClient.getCourseInfoDto(courseId);

        //通过远程调用根据用户id获取用户信息
        UcenterMemberOrder clientInfo = ucenterClient.getInfo(memberIdByJwtToken);

        //创建订单，向order对象里面设置需要的数据
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());//订单号
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfo.getTitle());
        order.setCourseCover(courseInfo.getCover());
        order.setTeacherName(courseInfo.getTeacherName());
        order.setTotalFee(courseInfo.getPrice());
        order.setMemberId(memberIdByJwtToken);
        order.setMobile(clientInfo.getMobile());
        order.setNickname(clientInfo.getNickname());
        order.setStatus(0);//支付状态0=未支付，1=已支付
        order.setPayType(1);//支付类型1=微信，2=支付宝
        baseMapper.insert(order);

        //返回订单号
        return order.getOrderNo();
    }
}
