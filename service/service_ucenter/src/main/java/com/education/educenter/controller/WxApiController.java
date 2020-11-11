package com.education.educenter.controller;

import com.education.commonutils.JwtUtils;
import com.education.educenter.entity.UcenterMember;
import com.education.educenter.service.UcenterMemberService;
import com.education.educenter.utils.ConstantWxUtils;
import com.education.educenter.utils.HttpClientUtils;
import com.education.servicebase.exceptionhandler.EducationException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@CrossOrigin
@Controller//注意这里没有配置 @RestController，因为这里不需要responsebody去返回数据，只想请求地址
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UcenterMemberService memberService;

    //1 生成微信二维码，不需要返回数据，所以不用返回R
    @GetMapping("login")
    public String getWxQrConnect(HttpSession session) {

        // 微信开放平台授权baseUrl，%s相当于？代表占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址，对redirect_url进行URLEncoder编码
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new EducationException(20001, "编码异常！");
        }

        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "Education";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        System.out.println("state = " + state);

        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟

        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                redirectUrl,
                state);

        return "redirect:" + qrcodeUrl;
    }

    //2 获取扫描人信息，添加数据
    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session) {

        //1 获取code值，临时票据，类似于验证码
        //得到授权临时票据code
        System.out.println("code = " + code);
        System.out.println("state = " + state);
        //从redis中将state获取出来，和当前传入的state作比较
        //如果一致则放行，如果不一致则抛出异常：非法访问

        //2 拿着code请求微信固定地址得到两个值accesstoken和openid
        //向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";

        //拼接三个参数：id 秘钥和code值
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_APP_SECRET,
                code);
        //请求这个拼接好的地址，得到返回两个值accesstoken和openid
        //使用httpclient发送请求，得到返回结果
        String accessTokenInfo = null;
        try {
            accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            System.out.println("accessToken=============" + accessTokenInfo);
        } catch (Exception e) {
            throw new EducationException(20001, "获取access_token失败");
        }

        //从accessTokenInfo字符串获取出来两个值accesstoken和openid
        //使用json转换工具Gson把accessTokenInfo字符串转换成map集合，根据key获取对应值
        //解析json字符串
        Gson gson = new Gson();
        HashMap map = gson.fromJson(accessTokenInfo, HashMap.class);
        String accessToken = (String) map.get("access_token");
        String openid = (String) map.get("openid");

        //查询数据库当前用用户是否曾经使用过微信登录
        UcenterMember member = memberService.getByOpenid(openid);
        if (member == null) {
            System.out.println("新用户注册");

            //3 拿着得到的accesstoken和openid，再去请求微信提供固定的地址，获取到扫描人的信息
            //访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            //拼接两个参数
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            //发送请求
            String userInfo = null;
            try {
                userInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + userInfo);
            } catch (Exception e) {
                throw new EducationException(20001, "获取用户信息失败");
            }

            //解析json，获取返回userInfo字符串扫描人信息
            HashMap<String, Object> mapUserInfo = gson.fromJson(userInfo, HashMap.class);
            String nickname = (String) mapUserInfo.get("nickname");
            String headimgurl = (String) mapUserInfo.get("headimgurl");

            //向数据库中插入一条记录
            member = new UcenterMember();
            member.setNickname(nickname);
            member.setOpenid(openid);
            member.setAvatar(headimgurl);
            memberService.save(member);
        }

        //使用jwt根据member对象生成token字符串
        // 生成jwt
        String token = JwtUtils.getJwtToken(member.getId(),member.getNickname());
        //存入cookie
        // CookieUtils.setCookie(request, response, "guli_jwt_token", token);
        //最后返回首页面，通过路径传递token字符串
        //因为端口号不同存在跨域问题，cookie不能跨域，所以这里使用url重写
        return "redirect:http://localhost:3000?token=" + token;
    }

}

