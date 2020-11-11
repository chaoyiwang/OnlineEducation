package com.education.eduservice.controller;

import com.education.commonutils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eduservice/user")
@CrossOrigin //解决跨域
public class EduLoginController {

    //Login
    @PostMapping("login")
    public R login(){
        return R.ok().data("token","admin");
    }
    //Info
    @GetMapping("info")
    public R info(){
        return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://www.ozbargain.com.au/themes/ozbargain/logo-icon-256.png");
    }

}
