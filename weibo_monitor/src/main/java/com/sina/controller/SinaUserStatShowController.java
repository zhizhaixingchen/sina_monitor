package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.SinaGroupMonitor;
import com.sina.pojo.SingleBlog;
import com.sina.service.SinaUserGroupService;
import com.sina.service.SinaUserStatService;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
//从es中获取监控内容
public class SinaUserStatShowController {
    //每一次显示15个
    private static final int SIZE = 5;
    private SinaUserStatService userStatService;
    private SinaUserGroupService userGroupService;
    public SinaUserStatShowController(SinaUserStatService userStatService, SinaUserGroupService userGroupService) {
        this.userStatService = userStatService;
        this.userGroupService = userGroupService;
    }

    @RequestMapping("/userStat")
    public ModelAndView getIndex(){
        ModelAndView mav = new ModelAndView("userStat");
        List<SinaGroupMonitor> allGroup = userGroupService.getAllGroup();
        mav.addObject("allGroup",allGroup);
        return mav;
    }

    //默认按照时间进行排序
    @RequestMapping("/userStat/default")
    @ResponseBody
    public String getdefault(@RequestParam(value = "from",defaultValue = "1")int from){
        List<SingleBlog> blogs = userStatService.queryDefault(--from*SIZE, SIZE);
        return JSON.toJSONString(blogs);
    }

    //按照分组进行排序
    @RequestMapping("/userStat/group")
    @ResponseBody
    public String getByGroup(int g_id,@RequestParam(value = "from",defaultValue = "1")int from){
        List<SingleBlog> blogs = userStatService.queryBlogByGroup(g_id,--from*SIZE,SIZE);
        return JSON.toJSONString(blogs);
    }

    //按照用户进行筛选
    @RequestMapping("/userStat/user")
    @ResponseBody
    public String getByUser(String name,@RequestParam(value = "from",defaultValue = "1")int from){
        List<SingleBlog> blogs = userStatService.queryBlogByUser(name,--from*SIZE,SIZE);
        return JSON.toJSONString(blogs);
    }

    //按照emotion进行筛选
    @RequestMapping("/userStat/emotion")
    @ResponseBody
    public String getByEmotion(String rotation,@RequestParam(value = "from",defaultValue = "1")int from){
        List<SingleBlog> blogs = null;
        if(rotation.equals("-")){
            blogs = userStatService.queryDefault(--from*SIZE,SIZE);
        }else{
            blogs = userStatService.queryBlogByEmotion(rotation,--from*SIZE,SIZE);
        }
        return JSON.toJSONString(blogs);
    }
}
