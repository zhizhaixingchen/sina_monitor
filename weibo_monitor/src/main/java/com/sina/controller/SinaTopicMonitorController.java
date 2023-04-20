package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.LayuiTableJson;
import com.sina.pojo.ScanTopicBean;
import com.sina.service.SinaTopicMonitorService;
import com.sina.service.SinaTopicdetailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//话题监控管理
@Controller
public class SinaTopicMonitorController {
    private SinaTopicdetailService topicdetailService;
    private SinaTopicMonitorService topicMonitorService;

    public SinaTopicMonitorController(SinaTopicdetailService topicdetailService, SinaTopicMonitorService topicMonitorService) {
        this.topicdetailService = topicdetailService;
        this.topicMonitorService = topicMonitorService;
    }

    @RequestMapping("/topicMonitorIndex")
    public ModelAndView topicMonitorIndex(HttpSession session){
        ModelAndView mav = new ModelAndView("topicMonitor");
        //查看内容  1.正在监控的项目 2.刚停止的项目  默认24小时以内
        long today = new Date().getTime();
        long yesterday = today-86400000;
        List<ScanTopicBean> scanList = topicMonitorService.getAllScan(yesterday,today);
        //存储至session 之后分页需要读取
        session.setAttribute("scanList",scanList);
        return mav;
    }


    //对监控数据进行分页
    @RequestMapping("/topic/monitorFresh")
    @ResponseBody
    public String getFresh(HttpSession session,int page,int limit) {
        String str = "";
        List<ScanTopicBean> list = (List<ScanTopicBean>) session.getAttribute("scanList");
        System.out.println("new scanList = "+list);
        //生成指定格式
        LayuiTableJson json = new LayuiTableJson();
        json.setCode(0);
        json.setMessage("");
        if(list!=null){
            int from = (page-1)*limit;
            int end = Math.min(page*limit,list.size());
            //切分 分页使用
            List<ScanTopicBean> finalList = list.subList(from, end);
            json.setData(finalList);
            json.setCount(list.size());
        }else{
            json.setCount(0);
        }
        str = JSON.toJSONString(json);
        return str;
    }


    //监控话题修改
    //打开监控 关闭监控等
    @RequestMapping("/topic/monitorUpdate")
    @ResponseBody
    public boolean monitorUpdate(String keyword,Boolean isMonitor,HttpSession session){
        //修改定时器
        if(isMonitor){
            topicdetailService.addMonitor(keyword);
        }else{
            topicdetailService.removeMonitor(keyword);
        }
        List<ScanTopicBean> list = (List<ScanTopicBean>)session.getAttribute("scanList");
        //修改es 并获取最新数据
        List<ScanTopicBean> allScan = topicMonitorService.updateTopicScan(keyword,isMonitor,list);

        //进行数据更新 暂为默认时间间隔       注意:由于ES存在读写延迟,如果我们更新数据后,立刻获取数据,获得的是脏数据
        //List<ScanTopicBean> allScan = topicScanService.getAllScan(10800000);
        //存储至session
        session.setAttribute("scanList",allScan);
        System.out.println("fresh");
        return true;
    }

    //根据选择的时间日期显示指定的监控
    @RequestMapping("/topic/monitor/queryByTimeRange")
    @ResponseBody
    public boolean queryByTimeRange(String begin,String end,HttpSession session){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long beginTime = 0;
        long endTime = 0;
        try {
            beginTime = sdf.parse(begin).getTime();
            endTime = sdf.parse(end).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //获取列表
        List<ScanTopicBean> allScan = topicMonitorService.getAllScan(beginTime, endTime);
        //更新session
        session.setAttribute("scanList",allScan);
        return true;
    }

    //根据content获取结果
    @RequestMapping("/topic/monitor/topicSearch")
    @ResponseBody
    public boolean queryBySearch(String keyword,HttpSession session){
        List<ScanTopicBean> allScan = null;
        if(keyword.length() == 0){
            long today = new Date().getTime();
            long yesterday = today-86400000;
            allScan = topicMonitorService.getAllScan(yesterday,today);
        }else{
            //获取列表
            allScan = topicMonitorService.searchByContent(keyword);
            //更新session
        }
        session.setAttribute("scanList",allScan);

        return true;
    }
}
