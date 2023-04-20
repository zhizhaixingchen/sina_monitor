package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.User;
import com.sina.service.SinaUserMessageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

/*
* 微博用户相关
* */
@Controller
public class SinaUserScanController {
//怎么确定该用户是当前监控用户？

    private SinaUserMessageService userService;
    public SinaUserScanController(SinaUserMessageService userService) {
        this.userService = userService;
    }

    //进入用户搜索界面
    @RequestMapping(value="/user/userIndex")
    public ModelAndView userIndex(HttpSession session){
        ModelAndView mav = new ModelAndView("userScan");
        Set<String> set  = (Set<String>) session.getServletContext().getAttribute("historyUserScan");
        mav.addObject("u_history",set);
        //获取最近搜索信息session
        return mav;
    }

    //查询用户  --->    全网查询
    @RequestMapping(value = "/user/checkuser",method = RequestMethod.GET)
    @ResponseBody
    public String checkUser(String username, HttpSession session){
        System.out.println("username="+username);
        String str = "";
        String userJson = "";
        String history = "";
        if(username.length()>0){
            List<User> userList = userService.searchUserFromInet(username);
            //将查询到的用户信息存储到tempUserList中,用户一部分信息在这里获取，一部分在详情页获得，用户发表的微博在微博界面获得
            session.setAttribute("tempUserList",userList);
            //对用户历史搜索列表进行修改
            Set<String> set = modifyHistoryUserList(session, username);
            userJson = JSON.toJSONString(userList);
            history = JSON.toJSONString(set);
        }
        str = "{\"userJson\":"+userJson+",\"history\":"+history+"}";
        return str;
    }

    private Set<String> modifyHistoryUserList(HttpSession session,String userName) {
        Set<String> set = (Set<String>) session.getServletContext().getAttribute("historyUserScan");
        if(set == null){
            set = new HashSet<>();
        }
        //追加新内容
        set.add(userName);
        if(set.size()>4){
            //随机删除一条内容
            String next = set.iterator().next();
            set.remove(next);
        }
        session.getServletContext().setAttribute("historyUserScan",set);
        return set;
    }
}
