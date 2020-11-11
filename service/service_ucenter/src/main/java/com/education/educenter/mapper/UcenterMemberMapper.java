package com.education.educenter.mapper;

import com.education.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2020-09-24
 */
public interface UcenterMemberMapper extends BaseMapper<UcenterMember> {
    //查询统计某一天的注册人数
    Integer selectRegisterCount(String day);
}
