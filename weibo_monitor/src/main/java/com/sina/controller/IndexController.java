package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.Loginuser;
import com.sina.pojo.SingleBlog;
import com.sina.service.LoginuserService;
import org.quartz.Scheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;


//主页面
@Controller
public class IndexController {
    private LoginuserService loginuserService;
    public IndexController(LoginuserService loginuserService) {
        this.loginuserService = loginuserService;
    }

    //主页 登录界面
    @RequestMapping("/index")
    public String getIndex(){
        return "index";
    }
    //进入其界面
    @RequestMapping("/main")
    public String getMain(){
        //是否登陆判断 之后使用拦截器
/*        Loginuser user = (Loginuser) session.getAttribute("loginuser");
        if(user == null){
            return "index";
        }*/
        //监控热搜榜
        return "main";
    }

    @RequestMapping("/main/cleanlog")
    @ResponseBody
    public boolean cleanlog(HttpSession session,String mail){
        System.out.println("mail = " + mail);
        //更新数据 mysql
        loginuserService.upDateLock(mail,5,"");
        //session
        Loginuser loginuser = (Loginuser) session.getAttribute("loginuser");
        loginuser.setLog("");
        session.setAttribute("loginuser",loginuser);
        return true;
    }

    @RequestMapping("/exit")
    public String cleanlog(HttpSession session){
        session.invalidate();
        return "index";
    }

    @RequestMapping("/test")
    public ModelAndView test(){
        ModelAndView mav = new ModelAndView("blogDetail");
        SingleBlog singleBlog = JSON.parseObject("{\"comments_num\":226,\"context\":\"#陈小纭微博小尾巴是关你peace#陈小纭微博小尾巴：关你peace，感觉这是要败光所剩无几的路人缘吧...R娱乐明星团的微博投票\u200B\u200B\u200B\u200B\",\"current_date\":1617962340510,\"emotion\":0.0,\"hot\":0,\"imgUrl\":\"https://tvax3.sinaimg.cn/crop.72.73.624.624.1024/7e10dc02ly8gdinwrqx9zj20jq0js403.jpg?KID=imgbed,tva&Expires=1617973138&ssig=qGbahRQMn6\",\"like\":50935,\"mid\":4624120837571568,\"myUrl\":\"https://weibo.com/2115034114/0Ka7uzvLI4?wvr=6\",\"name\":\"娱乐明星团\",\"pub_time\":\"2021-04-09 17:16\",\"transfer\":50,\"uid\":\"2115034114\"}\n", SingleBlog.class);
        mav.addObject("sinaBlog",singleBlog);
        return mav;
    }

}
