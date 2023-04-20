package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.LayuiTableJson;
import com.sina.pojo.SinaTopicESStatBean;
import com.sina.pojo.SinaHotTopicStatBean;
import com.sina.service.SinaTopicStatService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

//今日历史总览
@Controller
public class SinaTopicTodayStatController {

    private SinaTopicStatService statService;
    public SinaTopicTodayStatController(SinaTopicStatService statService) {
        this.statService = statService;
    }

    //进入该界面
    @RequestMapping(value = "/topic/statIndex",method = RequestMethod.GET)
    public String index(HttpSession session){
        //获取详细数据    不更改 可以这样写
        List<SinaTopicESStatBean> sinaTopicESStatBeans = statService.esFindAll("maxHot","desc");
        session.setAttribute("hotDetail",sinaTopicESStatBeans);
        System.out.println(sinaTopicESStatBeans);
        //获取统计数据
        List<SinaHotTopicStatBean> sinaHotTopicStatBeans = statService.FindAllstat();
        session.setAttribute("hotStat", sinaHotTopicStatBeans);
        return "topicstat";
    }

    //进行分页
    @RequestMapping(value = "/topic/statPage",method = RequestMethod.GET)
    @ResponseBody
    public String getPage(HttpSession session,int page,int limit) {
        String str = "";
        List<SinaTopicESStatBean> list = (List<SinaTopicESStatBean>) session.getAttribute("hotDetail");
        System.out.println("new scanList = "+list);
        //生成指定格式
        LayuiTableJson json = new LayuiTableJson();
        json.setCode(0);
        json.setMessage("");
        if(list!=null){
            int from = (page-1)*limit;
            int end = Math.min(page*limit,list.size());
            //切分 分页使用
            List<SinaTopicESStatBean> finalList = list.subList(from, end);
            json.setData(finalList);
            json.setCount(list.size());
        }else{
            json.setCount(0);
        }
        str = JSON.toJSONString(json);
        return str;
    }

    @RequestMapping(value = "/topic/statSort",method = RequestMethod.GET)
    @ResponseBody
    public boolean getSort(String field, String type, HttpSession session){
        System.out.println("field = " + field + ", type = " + type);
        //存储ES获取的数据
        //hot来自于结束时间,如果当前时间距离结束时间超过90s,则非热门.  同样我们可以用来进行排序
        if(field.equals("hot")){
            field = "endTime";
        }
        if(type.equals("")||type.length() == 0){
            field = "maxHot";
            type = "desc";
        }
        List<SinaTopicESStatBean> sinaTopicESStatBeans = statService.esFindAll(field,type);
        session.setAttribute("hotDetail",sinaTopicESStatBeans);
        return true;
    }

    //根据话题搜索
    @RequestMapping(value = "/topic/statESScan",method = RequestMethod.GET)
    @ResponseBody
    public boolean scanES(HttpSession session,String keyword) {
        System.out.println("keyword"+keyword);
        if(keyword.length() > 0){
            //展示搜索结果，精准匹配只有一个
            SinaTopicESStatBean statBean = statService.esFindByKeyword(keyword);
            List<SinaTopicESStatBean> list = new ArrayList<>();
            list.add(statBean);
            session.setAttribute("hotDetail",list);
        }else{
            //显示所有
            List<SinaTopicESStatBean> list = statService.esFindAll("maxHot","desc");
            session.setAttribute("hotDetail",list);
        }
        return true;
    }

    @RequestMapping(value = "/topic/statFreshAll",method = RequestMethod.GET)
    @ResponseBody
    public boolean freshAll(HttpSession session){
        System.out.println("更新数据");
        //存储ES获取的数据
        List<SinaTopicESStatBean> sinaTopicESStatBeans = statService.esFindAll("maxHot","desc");
        session.setAttribute("hotDetail",sinaTopicESStatBeans);
        //存储MYSQL获取的数据
        List<SinaHotTopicStatBean> sinaHotTopicStatBeans = statService.FindAllstat();
        session.setAttribute("hotStat", sinaHotTopicStatBeans);
        return true;
    }


}
