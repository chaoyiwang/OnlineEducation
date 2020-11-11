package com.education.educms.controller;

import com.education.commonutils.R;
import com.education.educms.entity.CrmBanner;
import com.education.educms.service.CrmBannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/educms/banner")
@Api(description = "网站首页Banner列表")
@CrossOrigin //跨域
public class BannerFrontController {
    @Autowired
    private CrmBannerService bannerService;

    //查询所有的banner
    @ApiOperation(value = "获取首页banner")
    @GetMapping("getAllBanner")
    public R getAllBanner() {
        List<CrmBanner> list = bannerService.selectAllBanner();
        return R.ok().data("bannerList", list);
    }

}

