package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.BlogMysql;
import com.sina.pojo.MonitorBlogES;
import com.sina.pojo.SinaComments;
import com.sina.pojo.SingleBlog;
import com.sina.service.SinaBlogCommentService;
import com.sina.service.SinaBlogMonitorService;
import com.sina.service.SinaBlogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

//微博详情页
@Controller
public class SinaBlogDetailController {
    private SinaBlogMonitorService blogMonitorService;
    private SinaBlogService blogService;
    private SinaBlogCommentService commentService;
    public SinaBlogDetailController(SinaBlogMonitorService blogMonitorService, SinaBlogService blogService, SinaBlogCommentService commentService) {
        this.blogMonitorService = blogMonitorService;
        this.blogService = blogService;
        this.commentService = commentService;
    }

    @RequestMapping(value = "/blog/detail",method = RequestMethod.GET)
    public ModelAndView getBlogDetail(String uid,String mid){
        ModelAndView mav = new ModelAndView("blogDetail");
        //获取指定微博信息     已包含情感信息
        SingleBlog sinaBlog = blogService.getSinaBlogFromInet(uid, mid);
        System.out.println(JSON.toJSONString(sinaBlog));
        //获取监控信息    如果对象为null，说明没有被监控
        BlogMysql blogMonitor = blogMonitorService.getBlogMonitorByMid(mid);
        mav.addObject("sinaBlog",sinaBlog);
        mav.addObject("blogMonitor",blogMonitor);
        //追加评论信息
        List<SinaComments> sinaComments = commentService.getSinaComments(Long.parseLong(mid));
        mav.addObject("sinaComments",sinaComments);
        //追加历史统计信息
        List<MonitorBlogES> monitorBlogData = blogMonitorService.getMonitorBlogData(Long.parseLong(mid));
        mav.addObject("blogData",monitorBlogData);
        return mav;
    }

    @RequestMapping(value = "/blog/comments",method = RequestMethod.GET)
    @ResponseBody
    public String getBlogComment(String mid){
        //获取评论信息        已包含情感值
        List<SinaComments> sinaComments = commentService.getSinaComments(Long.parseLong(mid));
        return JSON.toJSONString(sinaComments);
    }

    @RequestMapping(value = "/blog/monitorControl",method = RequestMethod.GET)
    @ResponseBody
    public boolean monitorController(boolean isOpen,String blogMysqlStr){
        BlogMysql blogMysql = JSON.parseObject(blogMysqlStr,BlogMysql.class);
        System.out.println(blogMysql);
        if(isOpen){
            //打开监控
            //追加开启监控时间 long
            blogMysql.setStarttime(System.currentTimeMillis());
            blogMonitorService.addMonitor(blogMysql);
        }else{
            //关闭监控
            blogMonitorService.deleteMonitor(String.valueOf(blogMysql.getMid()));
        }
        return true;
    }

    //未使用
    @RequestMapping(value = "/blog/monitorModify",method = RequestMethod.GET)
    @ResponseBody
    public boolean monitorModify(BlogMysql blogMysql){
        blogMonitorService.modifyMonitor(blogMysql);
        return true;
    }

    @RequestMapping(value = "/blog/esData",method=RequestMethod.GET)
    @ResponseBody
    public String monitorModify(long mid){
        List<MonitorBlogES> monitorBlogData = blogMonitorService.getMonitorBlogData(mid);
        return JSON.toJSONString(monitorBlogData);
    }

}
