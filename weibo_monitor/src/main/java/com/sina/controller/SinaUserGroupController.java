package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.LayuiTableJson;
import com.sina.pojo.SinaGroupMonitor;
import com.sina.service.SinaUserGroupService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

//针对用户分组进行管理
@Controller
public class SinaUserGroupController {
    private SinaUserGroupService userGroupService;
    public SinaUserGroupController(SinaUserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    //用户分组主界面
    @RequestMapping("/group")
    public ModelAndView userGroupIndex(HttpSession session){
        ModelAndView mav = new ModelAndView("userGroup");
        List<SinaGroupMonitor> allGroup = userGroupService.getAllGroup();
        session.setAttribute("allGroup",allGroup);
        mav.addObject("allGroup",allGroup);
        return mav;
    }
    //获取最新session数据
    @RequestMapping("/group/fresh")
    @ResponseBody
    public String freshGroup(HttpSession session){
        String str = "";
        List<SinaGroupMonitor> allGroup = (List<SinaGroupMonitor>) session.getAttribute("allGroup");
        LayuiTableJson json = new LayuiTableJson();
        json.setCode(0);
        json.setMessage("");
        json.setCount(allGroup.size());
        json.setData(allGroup);
        str += JSON.toJSONString(json);
        System.out.println(str);
        return str;
    }
    //修改信息  哪一行哪一个元素修改成什么
    @RequestMapping("/group/editGroup")
    @ResponseBody
    public String editTable(int g_id,String field,String value,HttpSession session){
        String str = "";
        boolean flag = true;
        List<SinaGroupMonitor> allGroup = (List<SinaGroupMonitor>) session.getAttribute("allGroup");
        SinaGroupMonitor group = getGroupByGId(allGroup,g_id);

        switch (field){
            case "g_name":
                 flag = checkOnly(allGroup, value);
                 if(flag){
                    group.setG_name(value);
                 }else{
                     str = "{\"status\":false,\"message\":\"存在同名分组，修改失败!!\"}";
                 }
                break;
            case "g_time":
                group.setG_time(Integer.parseInt(value));
                break;
            case "g_user_nummax":
                //修改的最大数目小于当前数目
                if(Integer.parseInt(value)<group.getG_user_numcur()){
                    str = "{\"status\":false,\"message\":\"修改的最大数目小于当前数目，修改失败!!\"}";
                    flag = false;
                }else{
                    group.setG_user_nummax(Integer.parseInt(value));
                }
                break;
        }
        if(flag){
            //更新group 数据库
            userGroupService.freshGroup(g_id,field,value);
            //更新group session
            session.setAttribute("allGroup",allGroup);
            str = "{\"status\":true,\"message\":\"修改成功\"}";
        }
        System.out.println("str = "+str);
        return str;
    }

    //追加分组
    @RequestMapping("/group/addGroup")
    @ResponseBody
    public String addGroup(String groupname,int freshtime,int maxnum,HttpSession session){
        String str = "";
        System.out.println("groupname = " + groupname + ", freshtime = " + freshtime + ", maxnum = " + maxnum);
        List<SinaGroupMonitor> allGroup = (List<SinaGroupMonitor>) session.getAttribute("allGroup");
        if(allGroup.size()>=7){
            str="{\"status\":"+false+",\"message\":\"分组已超上限，添加失败！！\"}";
        }else{
            boolean flag = checkOnly(allGroup,groupname);
            if(flag){
                //不存在同名
                //更新数据库
                userGroupService.addGroup(groupname,freshtime,maxnum);
                //更新session
                SinaGroupMonitor sgm = new SinaGroupMonitor(allGroup.get(allGroup.size()-1).getG_id()+1,groupname,freshtime,maxnum,0);
                allGroup.add(sgm);
                session.setAttribute("allGroup",allGroup);
                str="{\"status\":"+flag+",\"message\":\"分组添加成功\"}";
            }else{
                str="{\"status\":"+flag+",\"message\":\"分组名称重复，添加失败！！\"}";
            }
        }
        return str;
    }

    //删除用户分组
    @RequestMapping("/group/deleteGroup")
    @ResponseBody
    public boolean removeGroup(int key,HttpSession session){
        System.out.println("key = "+key);
        List<SinaGroupMonitor> allGroup = (List<SinaGroupMonitor>) session.getAttribute("allGroup");

        //数据库更新
        userGroupService.deleteGroup(key);
        //session更新
        //获取指定key
        SinaGroupMonitor group = getGroupByGId(allGroup, key);
        allGroup.remove(group);
        session.setAttribute("allGroup",allGroup);
        return true;
    }

    //判断同名分组
    //判断是否唯一
    private boolean checkOnly(List<SinaGroupMonitor> allGroup, String groupname) {
        boolean flag = true;
        for (SinaGroupMonitor sinaGroupMonitor : allGroup) {
            if(groupname.equals(sinaGroupMonitor.getG_name())){
                flag = false;
                break;
            }
        }
        return flag;
    }

    //根据g_id获取分组
    private SinaGroupMonitor getGroupByGId(List<SinaGroupMonitor> allGroup, int g_id) {
        SinaGroupMonitor sgm = null;
        for (SinaGroupMonitor sinaGroupMonitor : allGroup) {
            if (sinaGroupMonitor.getG_id() == g_id){
                sgm = sinaGroupMonitor;
                break;
            }
        }
        return sgm;
    }
}
