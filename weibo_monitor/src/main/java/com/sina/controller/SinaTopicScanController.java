package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaTopicBean;
import com.sina.service.SinaTopicScanService;
import com.sina.service.SinaTopicdetailService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.*;

//话题搜索控制
@Controller
public class SinaTopicScanController {

    private final SinaTopicScanService topicScanService;
    private SinaTopicdetailService topicdetailService;
    private Scheduler scheduler;
    public SinaTopicScanController(SinaTopicScanService topicScanService, SinaTopicdetailService topicdetailService, Scheduler scheduler) {
        this.topicScanService = topicScanService;
        this.topicdetailService = topicdetailService;
        this.scheduler = scheduler;
    }

//    话题搜索主界面
    @RequestMapping("/topicScanIndex")
    public ModelAndView getScanIndex(HttpSession session){
        ModelAndView mav = new ModelAndView("topicScan");
        ServletContext application = session.getServletContext();
        //先获取搜索历史
        Set<String> hisList = (Set<String>) application.getAttribute("historyScan");
        mav.addObject("historyScan", hisList);

        //获取监控历史
        List<String> hisMonitor= topicScanService.getHistory();
        Collections.reverse(hisMonitor);
        mav.addObject("historyMonitor",hisMonitor);
        return mav;
    }

    //搜索功能
    @RequestMapping(value = "/topic/scan",method = RequestMethod.GET)
    public ModelAndView getTopicScan(String keyword,HttpSession session){
        ModelAndView mav = new ModelAndView("topicDetail");
        SinaTopBean sinaTopBean = judgeTopBean(keyword,session);
        SinaTopicBean sinaTopicBean = topicdetailService.getSinaTopicByKeyWordFromInet(keyword,sinaTopBean);
        mav.addObject("sinaTopicBean",sinaTopicBean);
        //判断该话题是否被监控
        try {
            //判断job是否存在，如果存在则说明该话题正在被监控，如果不存在则说明该话题没有被监控
            boolean flag = scheduler.checkExists(new JobKey(sinaTopicBean.getKeyword(), "topic"));
            System.out.println("b = "+flag);
            System.out.println("标题="+sinaTopicBean.getKeyword());
            mav.addObject("isMon",flag);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        //将话题追加至application搜索历史记录中
        //将该话题添加至近期访问列表
        Set<String> hisSet = (Set<String>) session.getServletContext().getAttribute("historyScan");
        if(hisSet == null){
            hisSet = new HashSet<>();
        }
        if(hisSet.size() > 4){
            //最多5条 当为5条时随机删除一条
            String next = hisSet.iterator().next();
            hisSet.remove(next);
        }
        hisSet.add(keyword);
        session.getServletContext().setAttribute("historyScan",hisSet);
        return mav;
    }

    //判断当前话题是否是热门话题
    private SinaTopBean judgeTopBean(String keyword, HttpSession session) {
        List<SinaTopBean> sinaTopList = (List<SinaTopBean>) session.getAttribute("sinaTopList");
        if(sinaTopList != null){
            for (SinaTopBean sinaTopBean : sinaTopList) {
                if(keyword.equals(sinaTopBean.getKeyword())){
                    return sinaTopBean;
                }
            }
        }
        return null;
    }


    //话题搜索时联想
    @RequestMapping(value = "/topicscan/scanImage",method = RequestMethod.GET)
    @ResponseBody
    public String scanImagine(String keyword){
        System.out.println("keyword="+keyword);
        String str = "";
        List<String> strings = topicScanService.searchImagine(keyword);
        str=JSON.toJSONString(strings);
        return str;
    }
}
