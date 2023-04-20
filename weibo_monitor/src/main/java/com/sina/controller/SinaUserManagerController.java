package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.LayuiTableJson;
import com.sina.pojo.SinaGroupMonitor;
import com.sina.pojo.SinaUserMonitor;
import com.sina.service.SinaUserGroupService;
import com.sina.service.SinaUserMonitorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

//用户管理界面
@Controller
public class SinaUserManagerController {
    private SinaUserMonitorService userMonitorService;
    private SinaUserGroupService userGroupService;
    public SinaUserManagerController(SinaUserMonitorService userMonitorService, SinaUserGroupService userGroupService) {
        this.userMonitorService = userMonitorService;
        this.userGroupService = userGroupService;
    }

    //进入该界面
    @RequestMapping("/userManager")
    public ModelAndView getIndex(HttpSession session){
        ModelAndView mav = new ModelAndView("userManager");
        //获取所有用户
        List<SinaUserMonitor> list = userMonitorService.getAll();
        session.setAttribute("allUser",list);
        mav.addObject("allUser",list);
        //获取所有分组
        List<SinaGroupMonitor> allGroup = userGroupService.getAllGroup();
        session.setAttribute("allGroup",allGroup);
        mav.addObject("allGroup",allGroup);
        return mav;
    }

    //刷新界面，进行分页
    @RequestMapping(value = "/userManager/fresh",method = RequestMethod.GET)
    @ResponseBody
    public String getPage(HttpSession session,int page,int limit) {
        String str = "";
        List<SinaUserMonitor> list = (List<SinaUserMonitor>) session.getAttribute("allUser");
        System.out.println("new scanList = "+list);
        //生成指定格式
        LayuiTableJson json = new LayuiTableJson();
        json.setCode(0);
        json.setMessage("");
        if(list!=null){
            int from = (page-1)*limit;
            int end = Math.min(page*limit,list.size());
            //切分 分页使用
            List<SinaUserMonitor> finalList = list.subList(from, end);
            json.setData(finalList);
            json.setCount(list.size());
        }else{
            json.setCount(0);
        }
        str = JSON.toJSONString(json);
        return str;
    }

    //关闭用户监控
    @RequestMapping(value = "/userManager/stopMonitor",method = RequestMethod.GET)
    @ResponseBody
    public boolean stopMonitor(long u_id,HttpSession session) {
        List<SinaUserMonitor> allUser = (List<SinaUserMonitor>) session.getAttribute("allUser");
        SinaUserMonitor user = findUser(allUser, u_id);

        userMonitorService.deleteUserMonitor(user);

        List<SinaUserMonitor> newUserList = userMonitorService.getAll();
        List<SinaGroupMonitor> newGroupList = userGroupService.getAllGroup();
        //啊啊啊啊啊啊啊！这个bug隐藏的好深，我们每次更新数据后，只修改当前用户的GroupMonitor是不行的，所有与当前用户相同的GroupMonitor都需要进行修改，删除的时候也是这个道理
        session.setAttribute("allGroup",newGroupList);
        session.setAttribute("allUser",newUserList);
        return true;
    }

    //修改用户分组
    @RequestMapping(value = "/userManager/modifyGroup",method = RequestMethod.GET)
    @ResponseBody
    public String modifyGroup(long u_id,int g_id,HttpSession session) {
        String str = "";
        List<SinaUserMonitor> userList = (List<SinaUserMonitor>) session.getAttribute("allUser");
        //判断modifyGroup是否是当前分组
        SinaUserMonitor user = findUser(userList, u_id);
        if(user.getGroupMonitor().getG_id() == g_id){
            str = "{\"status\":false,\"message\":\"你已在当前分组中,移动失败!!\"}";
        }else{
            List<SinaGroupMonitor> allGroup = (List<SinaGroupMonitor>) session.getAttribute("allGroup");
            SinaGroupMonitor newGroup = findGroup(g_id, allGroup);
            //判断分组是否还有容量
            if(newGroup.getG_user_numcur()==newGroup.getG_user_nummax()){
                str = "{\"status\":false,\"message\":\"当前分组已满,移动失败!!\"}";
            }else{
                userMonitorService.modifyGroupUserMonitor(user,g_id);

                List<SinaUserMonitor> newUserList = userMonitorService.getAll();
                List<SinaGroupMonitor> newGroupList = userGroupService.getAllGroup();
                //啊啊啊啊啊啊啊！这个bug隐藏的好深，我们每次更新数据后，只修改当前用户的GroupMonitor是不行的，所有与当前用户相同的GroupMonitor都需要进行修改，删除的时候也是这个道理
                session.setAttribute("allGroup",newGroupList);
                session.setAttribute("allUser",newUserList);
                str = "{\"status\":true,\"message\":\"修改分组成功!!\"}";
            }
        }

        return str;
    }

    //根据uid查找用户
    private SinaUserMonitor findUser(List<SinaUserMonitor> list, long key) {
        SinaUserMonitor userMonitor = null;
        for (SinaUserMonitor sinaUserMonitor : list) {
            if(sinaUserMonitor.getU_id() == key){
                userMonitor = sinaUserMonitor;
                break;
            }
        }
        return userMonitor;
    }

    //根据gid查找分组
    private SinaGroupMonitor findGroup(int g_id,List<SinaGroupMonitor> list) {
        SinaGroupMonitor sgm = null;
        for (SinaGroupMonitor sinaGroupMonitor : list) {
            if(sinaGroupMonitor.getG_id() == g_id){
                sgm = sinaGroupMonitor;
                break;
            }
        }
        return sgm;
    }


}
