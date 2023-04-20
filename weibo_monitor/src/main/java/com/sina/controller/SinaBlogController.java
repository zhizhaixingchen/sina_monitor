package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sina.pojo.BlogMysql;
import com.sina.pojo.SingleBlog;
import com.sina.service.SinaBlogMonitorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

//新浪微博 信息
@Controller
public class SinaBlogController {
    private SinaBlogMonitorService blogMonitorService;
    private static final int SIZE = 5;
    public SinaBlogController(SinaBlogMonitorService blogMonitorService) {
        this.blogMonitorService = blogMonitorService;
    }

    //进入主界面  监控微博概览界面
    @RequestMapping(value = "/blog",method = RequestMethod.GET)
    public String getAllMonitorBlog(){
        return "blogsummary";
    }

    //查询监控微博
    @RequestMapping(value = "/blog/all",method = RequestMethod.GET)
    @ResponseBody
    public String getAllBlog(@RequestParam(value = "from",defaultValue = "1") int from){
        Map<String,Object> singleBlogAndBlogMysql = blogMonitorService.getAllMonitorBlog(--from*SIZE, SIZE);
        String str = getJsonStr(singleBlogAndBlogMysql);
        return str;
    }

    //根据用户名进行筛选
    @RequestMapping(value = "/blog/name",method = RequestMethod.GET)
    @ResponseBody
    public String getBlogByName(String name,@RequestParam(value = "from",defaultValue = "1")int from){
        Map<String,Object> singleBlogAndBlogMysql = blogMonitorService.getMonitorBlogByName(name,--from*SIZE,SIZE);
        String str = getJsonStr(singleBlogAndBlogMysql);
        return str;
    }

    //根据时间进行筛选
    @RequestMapping(value = "/blog/time",method = RequestMethod.GET)
    @ResponseBody
    public String getBlogByTime(long start,long end,@RequestParam(value = "from",defaultValue = "1")int from){

        System.out.println("start = " + start + ", end = " + end + ", from = " + from);
        Map<String,Object> singleBlogAndBlogMysql = blogMonitorService.getMonitorBlogByTime(start,end,--from*SIZE,SIZE);
        String str = getJsonStr(singleBlogAndBlogMysql);
        System.out.println("str = "+str);
        return str;
    }

    //根据情感值进行筛选
    @RequestMapping(value = "/blog/emotion",method = RequestMethod.GET)
    @ResponseBody
    public String getBlogByEmotion(String rotation,@RequestParam(value = "from",defaultValue = "1")int from){
        Map<String, Object> singleBlogAndBlogMysql = null;
        if(rotation.equals("-")){
            singleBlogAndBlogMysql = blogMonitorService.getAllMonitorBlog(--from, SIZE);
        }else{
            singleBlogAndBlogMysql = blogMonitorService.getMonitorBlogByEmotion(rotation,--from*SIZE,SIZE);
        }
        String str = getJsonStr(singleBlogAndBlogMysql);
        return str;
    }

    //获取当前正在监控的微博
    @RequestMapping(value = "/blog/monitor",method = RequestMethod.GET)
    @ResponseBody
    public String getBlogByMonitor(@RequestParam(value = "from",defaultValue = "1")int from){
        Map<String,Object> singleBlogAndBlogMysql = blogMonitorService.getMonitorBlogByMonitor(--from*SIZE,SIZE);
        String str = getJsonStr(singleBlogAndBlogMysql);
        return str;
    }


    private String getJsonStr(Map<String, Object> singleBlogAndBlogMysql) {
        List<SingleBlog> singleBlogList = (List<SingleBlog>) singleBlogAndBlogMysql.get("singleBlogList");
        List<BlogMysql> blogMysqlList = (List<BlogMysql>) singleBlogAndBlogMysql.get("blogMysqlList");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("singleBlogList",singleBlogList);
        jsonObject.put("blogMysqlList",blogMysqlList);
        String result = JSON.toJSONString(jsonObject);
        return result;
    }
}
