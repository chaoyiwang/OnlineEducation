package com.education.educenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.commonutils.JwtUtils;
import com.education.commonutils.MD5;
import com.education.educenter.entity.UcenterMember;
import com.education.educenter.entity.vo.RegisterVo;
import com.education.educenter.mapper.UcenterMemberMapper;
import com.education.educenter.service.UcenterMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.servicebase.exceptionhandler.EducationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-24
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //登录的方法
    @Override
    public String login(UcenterMember member) {
        //获取登录手机和密码
        String mobile = member.getMobile();
        String password = member.getPassword();

        //校验参数，判断手机号和密码非空，\\表示或者
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new EducationException(20001,"手机号或密码不能为空！登录失败！");
        }

        //获取会员，判断手机号是否正确
        UcenterMember mobileMember = baseMapper.selectOne(new QueryWrapper<UcenterMember>().eq("mobile", mobile));
        if(mobileMember == null) {
            throw new EducationException(20001,"手机号不存在！登录失败！");
        }

        //校验密码，判断密码是否正确
        if(!MD5.encrypt(password).equals(mobileMember.getPassword())) {
            throw new EducationException(20001,"密码错误！登录失败！");
        }

        //校验是否被禁用，true说明被禁用
        if(mobileMember.getIsDisabled()) {
            throw new EducationException(20001,"账号暂不可用！登录失败！");
        }

        //使用JWT生成token字符串，返回token值帮助单点登录
        String token = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        return token;

    }

    //注册
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册信息
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        //校验参数
        if(StringUtils.isEmpty(mobile) ||
                StringUtils.isEmpty(mobile) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(code)) {
            throw new EducationException(20001,"注册内容不能为空！");
        }

        //校验校验验证码
        //根据手机号从redis获取发送的验证码
        String mobleCode = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(mobleCode)) {
            throw new EducationException(20001,"验证码错误！");
        }

        //查询数据库中是否存在相同的手机号码，如果表里存在相同的手机号，那无法进行添加
        Integer count = baseMapper.selectCount(new QueryWrapper<UcenterMember>().eq("mobile", mobile));
        if(count.intValue() > 0) {
            throw new EducationException(20001,"error");
        }

        //添加注册信息到数据库
        UcenterMember member = new UcenterMember();
        member.setNickname(nickname);
        member.setMobile(registerVo.getMobile());
        member.setPassword(MD5.encrypt(password));
        member.setIsDisabled(false);
        member.setAvatar("https://www.ozbargain.com.au/themes/ozbargain/logo-icon-256.png");
        baseMapper.insert(member);
    }

    //根据openid查询数据库当前用用户是否曾经使用过微信登录
    @Override
    public UcenterMember getByOpenid(String openid) {
        QueryWrapper<UcenterMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);

        UcenterMember member = baseMapper.selectOne(queryWrapper);
        return member;
    }
    //查询统计某一天的注册人数
    @Override
    public Integer countRegisterByDay(String day) {
        return baseMapper.selectRegisterCount(day);
    }
}
