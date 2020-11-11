package com.education.educenter.controller;


import com.education.commonutils.JwtUtils;
import com.education.commonutils.R;
import com.education.commonutils.UcenterMemberOrder;
import com.education.educenter.entity.UcenterMember;
import com.education.educenter.entity.vo.RegisterVo;
import com.education.educenter.service.UcenterMemberService;
import com.education.servicebase.exceptionhandler.EducationException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-09-24
 */
@RestController
@RequestMapping("/educenter/member")
@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    //登录
    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R loginUser(@RequestBody UcenterMember member) {
        String token = memberService.login(member);
        return R.ok().data("token", token);
    }

    //注册
    @ApiOperation(value = "会员注册")
    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo){
        memberService.register(registerVo);
        return R.ok();
    }

    //根据token获取登录信息
    @ApiOperation(value = "根据token获取登录信息")
    @GetMapping("getLoginInfo")
    public R getLoginInfo(HttpServletRequest request){
        try {
            //调用jwt工具类的方法。根据request对象获取头信息，返回用户id
            String memberId = JwtUtils.getMemberIdByJwtToken(request);
            //查询数据库，根据用户id获取数据库信息
            UcenterMember member = memberService.getById(memberId);
            return R.ok().data("userInfo", member);
        }catch (Exception e){
            e.printStackTrace();
            throw new EducationException(20001,"获取不到登录信息！");
        }
    }

    //根据用户id获取用户信息，返回用户信息对象
    //根据token字符串获取用户信息
    @PostMapping("getInfoUc/{id}")
    public UcenterMemberOrder getInfo(@PathVariable String id) {
        //根据用户id获取用户信息
        UcenterMember member = memberService.getById(id);
        //把member对象里面放值复制给Ucentermember对象
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member,ucenterMemberOrder);
        return ucenterMemberOrder;
    }
    //查询统计某一天的注册人数
    @GetMapping(value = "countregister/{day}")
    public R registerCount(
            @PathVariable String day){
        Integer count = memberService.countRegisterByDay(day);
        return R.ok().data("countRegister", count);
    }
}

