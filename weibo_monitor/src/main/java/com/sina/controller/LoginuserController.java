package com.sina.controller;

import com.alibaba.fastjson.JSONObject;
import com.sina.pojo.Loginuser;
import com.sina.service.LoginuserService;
import com.sina.util.MailUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller
public class LoginuserController {

    private LoginuserService loginuserService;
    public LoginuserController(LoginuserService loginuserService) {
        this.loginuserService = loginuserService;
    }

    @RequestMapping(value = "/index/checkLogin",method = RequestMethod.POST)
    @ResponseBody
    public String checkLogin(HttpSession session,String mail, String password){
        System.out.println("mail = " + mail + ", password = " + password);
        Loginuser loginuser = loginuserService.checkLoginCorrect(mail);
        JSONObject jsonObject = new JSONObject();
        if(loginuser == null){
            jsonObject.put("status",false);
            jsonObject.put("message","该用户不存在，登陆失败");
        }else{
            boolean ps_true = judgePasswordCorrect(password, loginuser.getPassword());
            if(loginuser.getLock()==0){
                jsonObject.put("status",false);
                jsonObject.put("message","该账户已被锁定，登陆失败！");
            }else{
                if(ps_true){
                    loginuser.setPassword("");
                    loginuser.setLock(0);
                    jsonObject.put("status",true);
                    jsonObject.put("message","");
                    //追加至session
                    session.setAttribute("loginuser",loginuser);
                }else{
                    loginuserService.upDateLock(mail,loginuser.getLock()-1,"密码错误");
                    jsonObject.put("status",false);
                    int tryTime = loginuser.getLock()-1;
                    if(tryTime == 0){
                        jsonObject.put("message","密码错误，该账户已被锁定");
                    }else{
                        jsonObject.put("message","密码错误，你还可以尝试"+tryTime+"次");
                    }

                }
            }
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/index/register",method = RequestMethod.POST)
    @ResponseBody
    public String checkRegister(HttpSession session,String username,String password,String mail,String validate){
        //校验码比较
        String randomNum = (String) session.getAttribute("validate");
        System.out.println("randomNum = "+randomNum);
        JSONObject jsonObject = new JSONObject();
        if(!randomNum.equals(validate)){
            jsonObject.put("status",false);
            jsonObject.put("message","验证码输入有误");
            return jsonObject.toJSONString();
        }
        Loginuser lu = new Loginuser(mail,username,password,(byte)0,(byte)0,(byte)0,(byte)0, 4,"初与君相识，犹如故人归。");
        loginuserService.insertUser(lu);
        jsonObject.put("status",true);
        jsonObject.put("message","注册成功，将于6秒后进入主界面");
        lu.setPassword("");
        lu.setLock(0);
        session.setAttribute("loginuser",lu);
        return jsonObject.toJSONString();
    }

    //发送邮件
    @RequestMapping(value = "/index/registerMail",method = RequestMethod.GET)
    @ResponseBody
    public boolean getRegisterMail(HttpSession session,String mail){
        //mail重复性比较
        Loginuser loginuser = loginuserService.checkLoginCorrect(mail);
        if(loginuser!=null){
            return false;
        }
/*        //否则发送注册邮件
        String title="微鱼用户注册验证";
        String randomNum = "";
        for(int i = 0;i<6;i++){
            randomNum+=(int)(Math.random()*10);
        }
        String content = "欢迎您使用微鱼舆情监测系统，本次注册的验证码为"+randomNum+",10分钟内有效。";

        System.out.println("randomNum = "+randomNum);
        MailUtil mailUtil = new MailUtil(mail, title, content);
        new Thread(mailUtil).start();*/
        //发送注册邮件
        String randomNum = loginuserService.sendRegisterMail(mail);
        session.setAttribute("validate",randomNum);
        return true;
    }

    boolean judgePasswordCorrect(String judge,String correct){
        boolean flag = false;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(judge.getBytes("UTF8"));
            byte s[ ]=m.digest( );
            String result="";
            for (int i=0; i<s.length;i++){
                result+=Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
            }
            if(result.equals(correct)){
                flag = true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
