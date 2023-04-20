package com.sina.controller;

import com.sina.pojo.SinaGroupMonitor;
import com.sina.pojo.SinaUserMonitor;
import com.sina.pojo.SingleBlog;
import com.sina.pojo.User;
import com.sina.service.SinaBlogService;
import com.sina.service.SinaUserGroupService;
import com.sina.service.SinaUserMessageService;
import com.sina.service.SinaUserMonitorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
//注：用户监控，主要监控的是用户是否发表微博 以便在第一时间知道 这个不存储进数据库
//原因 存储数据库意味着需要持续记录某些东西，针对一个用户而言，我们通过分析其近期微博可以获取用户近况信息，需要持续记录的只有粉丝数目、关注数目和发表微博数目的变化，这对监控来说没太大意义，所以用户层面，不需要访问ES数据库


//注意：由于我们在进入详细信息界面时已经获取了可以使用的分组，这些分组还有容量，因此 我们不论添加还是修改都不会造成分组超容量的情况。
@Controller
public class SinaUserDetailController {
    //用户基本信息
    private SinaUserMessageService userMessageService;
    //用户监控信息
    private SinaUserMonitorService userMonitorService;
    //用户微博信息
    private SinaBlogService sinaBlogService;
    //分组信息
    private SinaUserGroupService userGroupService;
    public SinaUserDetailController(SinaUserMessageService userMessageService, SinaUserMonitorService userMonitorService, SinaBlogService sinaBlogService, SinaUserGroupService userGroupService) {
        this.userMessageService = userMessageService;
        this.userMonitorService = userMonitorService;
        this.sinaBlogService = sinaBlogService;
        this.userGroupService = userGroupService;
    }

    //查看用户详细    --->    进入用户微博界面
    //默认展示用户微博
    @RequestMapping(value = "/user/detail",method = RequestMethod.GET)
    public ModelAndView userDetail(long uid, HttpSession session){
        ModelAndView mav = new ModelAndView("userDetail");
        User u = getUserBasicMessageFromSession(uid,session);
        //获取用户详细信息
        User detailUser = userMessageService.spiderUserDetail(u);
        //获取用户监控信息  是否被监控   监控分组等   mysql
        SinaUserMonitor userMonitor = userMonitorService.getUserMonitor(uid);
        //如果用户监控为null,则需要创建
        if(userMonitor == null){
            userMonitor = initUserMonitor(detailUser);
        }
        //获取用户最近的微博信息   爬虫
        List<SingleBlog> blogList = sinaBlogService.getSinaBlogListFromInet(uid,detailUser.getCurrentBlog());
        //获取分组列表
        List<SinaGroupMonitor> allGroup = userGroupService.getCanUseGroup();

        mav.addObject("detailUser",detailUser);
        //将当前用户存入session，方便后续开启监控，删除监控
        session.setAttribute("userMonitor",userMonitor);
        mav.addObject("userMonitor",userMonitor);
        mav.addObject("blogList",blogList);
        mav.addObject("allGroup",allGroup);
        return mav;
    }

    //根据uid查询保存在session中的用户     获取用户基本信息
    private User getUserBasicMessageFromSession(long uid, HttpSession session) {
        List<User> userList = (List<User>) session.getAttribute("tempUserList");
        for (User user : userList) {
            if(uid == user.getUid()){
                return user;
            }
        }
        return null;
    }


    //更改用户监控信息  打开监控 关闭监控
    @RequestMapping(value = "/user/changemonitor",method = RequestMethod.GET)
    @ResponseBody
    public boolean changeMonitor(boolean isMonitor,HttpSession session){
        SinaUserMonitor userMonitor = (SinaUserMonitor) session.getAttribute("userMonitor");
        //monitor   打开监控
        if(isMonitor){
            //默认为默认分组
            SinaUserMonitor newMonitor = userMonitorService.createUserMonitor(userMonitor, 1);
            //更新session中监控信息
            session.setAttribute("userMonitor",newMonitor);
        }else{
            //monitor   关闭监控
            SinaUserMonitor newMonitor = userMonitorService.deleteUserMonitor(userMonitor);
            //更新session中监控信息
            session.setAttribute("userMonitor",newMonitor);
        }
        return true;
    }

    //修改监控分组
    @RequestMapping(value = "/user/modifymonitor",method = RequestMethod.GET)
    @ResponseBody
    public boolean modifyMonitorGroup(int g_id,boolean mon,HttpSession session){
        SinaUserMonitor userMonitor = (SinaUserMonitor) session.getAttribute("userMonitor");
        SinaUserMonitor newMonitor = null;
        if(mon){
            //除去伪修改状态  比如原来为默认 修改为默认
            if(g_id!=userMonitor.getGroupMonitor().getG_id()){
                newMonitor = userMonitorService.modifyGroupUserMonitor(userMonitor, g_id);
            }else{
                newMonitor = userMonitor;
                //说明修改失败
                return false;
            }
        }else{
            newMonitor = userMonitorService.createUserMonitor(userMonitor,g_id);
        }
        session.setAttribute("userMonitor",newMonitor);
        return true;
    }

    //跳转至用户监控界面
    @RequestMapping(value = "/user/touserdetail",method = RequestMethod.GET)
    public ModelAndView toUserDetail(long uid,String uname,HttpSession session){
        User user = null;
        ModelAndView mav = new ModelAndView("userDetail");
        //先判断用户是否存在于mysql中
        SinaUserMonitor userMonitor = userMonitorService.getUserMonitor(uid);
        if(userMonitor == null){
            //两步走获取user  获取基本信息
            List<User> userList = userMessageService.searchUserFromInet(uname);
            user = userList.get(0);
        }else{
            //基本信息
            user = new User(userMonitor.getU_id(),userMonitor.getU_name(),userMonitor.getU_imgUrl(),userMonitor.getU_host(),userMonitor.getU_location(),"","","");
        }
        //详细信息
        user = userMessageService.spiderUserDetail(user);
        //如果用户监控为null,则需要创建
        if(userMonitor == null){
            userMonitor = initUserMonitor(user);
        }
        //获取用户最近的微博信息   爬虫
        List<SingleBlog> blogList = sinaBlogService.getSinaBlogListFromInet(uid,user.getCurrentBlog());
        //获取分组列表
        List<SinaGroupMonitor> allGroup = userGroupService.getCanUseGroup();

        mav.addObject("detailUser",user);
        //将当前用户存入session，方便后续开启监控，删除监控
        session.setAttribute("userMonitor",userMonitor);
        mav.addObject("userMonitor",userMonitor);
        mav.addObject("blogList",blogList);
        mav.addObject("allGroup",allGroup);
        return mav;
    }

    //初始化userMonitor对象
    private SinaUserMonitor initUserMonitor(User user) {

        SinaUserMonitor sum = new SinaUserMonitor(user.getUid(),user.getUsername(),Long.parseLong(user.getFans()),Long.parseLong(user.getBlogNum()),null,user.getLocation(),user.getHost(),user.getImgUrl());
        return sum;
    }

/*    //将万亿转换为数字
    private String translate(String words){
        String str = "";
        if(words.endsWith("万")){
            str = words.split("万")[0]+"0000";;
        }else if(words.endsWith("亿")){
            str = words.split("亿")[0]+"00000000";
        }else{
            str = words;
        }
        return str;
    }*/



}
