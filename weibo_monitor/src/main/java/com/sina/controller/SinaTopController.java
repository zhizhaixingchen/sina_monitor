package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.LayuiTableJson;
import com.sina.pojo.SinaTopBean;
import com.sina.service.SinaTopService;
import com.sina.util.sina.SinaHotTop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class SinaTopController {
    @Autowired
    private SinaTopService sinaTopService;

    //获取当前热度帮最新数据
    @RequestMapping("/hot/summary")
    @ResponseBody
    public String getHotSummary(HttpSession session){
        List<SinaTopBean> sinaTopList = sinaTopService.getSinaTopFromInet();
        //将热搜存入session  作用：如果登上热门，在显示热门信息时，那么我们不需要再对其进行情感倾向性判断 SinaTopicController
        session.setAttribute("sinaTopList",sinaTopList);
        return JSON.toJSONString(sinaTopList);
    }

    //从数据库获取热搜信息
    @RequestMapping(value = "/hot/getHotOne",method = RequestMethod.GET)
    @ResponseBody
    public String hotOne(String content){
        List<SinaTopBean> sinaTopFromDB = sinaTopService.getSinaTopFromDB(content);
        String str = JSON.toJSONString(sinaTopFromDB);
        System.out.println("hot = "+str);
        return str;
    }



    //微博热搜界面
    @RequestMapping("/hot")
    public String getHot(){
        return "hotTopic";
    }

    //更新热搜
    @RequestMapping("/hot/fresh")
    @ResponseBody
    public String getFresh(HttpSession session,int page,int limit) {
        String str = "";
        List<SinaHotTop> list = (List<SinaHotTop>) session.getAttribute("sinaTopList");
        //生成指定格式
        LayuiTableJson json = new LayuiTableJson();
        json.setCode(0);
        json.setMessage("");
        if(list!=null){
            int from = (page-1)*limit;
            int end = Math.min(page*limit,list.size());
            //切分 分页使用
            List<SinaHotTop> finalList = list.subList(from, end);
            json.setData(finalList);
            json.setCount(list.size());
        }else{
            json.setCount(0);
        }
        str = JSON.toJSONString(json);
        return str;
    }
}
