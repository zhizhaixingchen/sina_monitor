package com.sina.controller;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.LayuiTableJson;
import com.sina.pojo.Loginuser;import com.sina.service.LoginuserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginuserManageController {
    private LoginuserService loginuserService;

    public LoginuserManageController(LoginuserService loginuserService) {
        this.loginuserService = loginuserService;
    }

    @RequestMapping("/loginuser")
    public String getAll(){
        return "loginuserManager";
    }

    //状态管理
    @RequestMapping("/loginuser/manage")
    @ResponseBody
    public boolean manage(HttpSession session,String mail,String field,boolean status){
        System.out.println("mail = " + mail + ", field = " + field + ", status = " + status);
        Loginuser loginuser = (Loginuser) session.getAttribute("loginuser");
/*        Loginuser loginuser = new Loginuser();
        loginuser.setMail("180484565@qq.com");
        loginuser.setName("杜虹凯");*/
        boolean flag = loginuserService.updatePower(mail,field,status,loginuser);
        return flag;
    }

    //删除用户

    @RequestMapping("/loginuser/delete")
    @ResponseBody
    public boolean delete(HttpSession session,String mail){
        Loginuser loginuser = (Loginuser) session.getAttribute("loginuser");
/*        Loginuser loginuser = new Loginuser();
        loginuser.setMail("1830484565@qq.com");
        loginuser.setName("杜虹凯");*/
        boolean flag = loginuserService.deleteUser(mail,loginuser);
        return flag;
    }

    @RequestMapping("/loginuser/fresh")
    @ResponseBody
    public String getNewMessage(int page,int limit){
        List<Loginuser> list = loginuserService.getAllLoginuser();
        //生成指定格式
        LayuiTableJson json = new LayuiTableJson();
        json.setCode(0);
        json.setMessage("");
        if(list!=null){
            int from = (page-1)*limit;
            int end = Math.min(page*limit,list.size());
            //切分 分页使用
            List<Loginuser> finalList = list.subList(from, end);
            json.setData(finalList);
            json.setCount(list.size());
        }else{
            json.setCount(0);
        }
        return JSON.toJSONString(json);
    }



}
