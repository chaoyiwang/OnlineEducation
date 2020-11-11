package com.education.educenter.service;

import com.education.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.educenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-09-24
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    //登录的方法
    String login(UcenterMember member);
    //注册
    void register(RegisterVo registerVo);
    //根据openid查询数据库当前用用户是否曾经使用过微信登录
    UcenterMember getByOpenid(String openid);
    //查询统计某一天的注册人数
    Integer countRegisterByDay(String day);
}
