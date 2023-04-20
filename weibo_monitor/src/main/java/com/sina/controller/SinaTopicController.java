package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaTopicBean;
import com.sina.service.SinaTopicScanService;
import com.sina.service.SinaTopicdetailService;

import org.quartz.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class SinaTopicController {

    private SinaTopicdetailService topicdetailService;
    private Scheduler scheduler;
    private SinaTopicScanService topicScanService;
    public SinaTopicController(SinaTopicdetailService topicdetailService, Scheduler scheduler, SinaTopicScanService topicScanService) {
        this.topicdetailService = topicdetailService;
        this.scheduler = scheduler;
        this.topicScanService = topicScanService;
    }

   //从热榜跳转
    //跳转至topicDetail.html
    //携带数据sinaTopicBean
    @RequestMapping(value = "/topic/hot",method = RequestMethod.GET)
    //参数                    跳转时是第几条
    //情感分析关闭位置  1.sinaHotTop    2.sinaCommonTopic
    //关闭词云  1.sinaHotTop
    public ModelAndView getHotTopic(int stage, HttpSession session){
        List<SinaTopBean> sinaTopList = (List<SinaTopBean>) session.getAttribute("sinaTopList");
        SinaTopBean sinaTopBean = sinaTopList.get(stage);
        String keyword = sinaTopBean.getKeyword();
        SinaTopicBean sinaTopicBean = topicdetailService.getSinaTopicByKeyWordFromInet(keyword,sinaTopBean);
        ModelAndView mav = new ModelAndView("topicDetail");
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


    //获取该话题今日所有数据       -->elasticsearch
    @RequestMapping(value = "/topic/today",method = RequestMethod.GET)
    @ResponseBody
    public String getTodayData(String keyword){
        List<SinaTopicBean> sinaTopicList = topicdetailService.getSinaTopicByKeywordFromDB(keyword);
        String jsonList = JSON.toJSONString(sinaTopicList);
        return jsonList;
    }

    @RequestMapping(value = "/topic/monitor",method = RequestMethod.GET)
    @ResponseBody
    public boolean monitor(boolean mon,String topicName){
        System.out.println("mon = " + mon + ", topicName = " + topicName);
        if(mon){
            //话题监控状态逻辑
            //topicName作为JobDetailname标识
            topicdetailService.addMonitor(topicName);
        }else{
            //话题非监控状态逻辑
            topicdetailService.removeMonitor(topicName);
        }
        topicScanService.addTopicScan(topicName,mon);
        return true;
    }

//    定时器模板
    /*@Autowired
    private Scheduler scheduler;

@RequestMapping("/test")
    public String test(){
    //job任务
    JobDetail jobDetail = JobBuilder
            .newJob(TestJob.class)
            .withIdentity("jobName1","jobGroup1")
            .usingJobData("data","当前时间-->"+new Date())
            .usingJobData("data2","hello-->"+new Date())
            .build();

    Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("triggle-1","triggler")
            .startNow()
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever())
            .build();
    try {
        scheduler.scheduleJob(jobDetail,trigger);
        scheduler.start();
    } catch (SchedulerException e) {
        e.printStackTrace();
    }
    return "topicDetail";
}

*/
/*    @Autowired
    private JestClient jestClient;
@RequestMapping("/test2")
    public void test2(){
    Cat cat = new Cat.IndicesBuilder().addIndex("sina").build();
    try {
        JestResult result = jestClient.execute(cat);
        String s = result.toString();
        System.out.println(s);
    } catch (IOException e) {
        e.printStackTrace();
    } catch (JsonSyntaxException e){
        System.out.println("不存在");
    }
}*/
    //停止定时器
    @RequestMapping("/test2")
    public void test2() {
        try {
            scheduler.clear();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }



    //话题监控列表 用来控制不同话题的更新情况
    //将该话题添加至监控列表       -->mysql

    //将该话题从监控列表中移除      -->mysql

    //展示话题监控列表            -->mysql


}
