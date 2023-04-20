package com.sina.service.impl;

import com.sina.dao.mapper.LoginuserMapper;
import com.sina.pojo.Loginuser;
import com.sina.service.LoginuserService;
import com.sina.util.MailUtil;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LoginuserServiceImpl implements LoginuserService {

    private LoginuserMapper loginuserMapper;
    public LoginuserServiceImpl(LoginuserMapper loginuserMapper) {
        this.loginuserMapper = loginuserMapper;
    }


    //判断账户的正确性
    // return null 用户不存在
    // return Loginuser
    // lock = 0 账户锁定 不能登录           注：这里需要设置个定时器进行恢复锁定
    @Override
    public Loginuser checkLoginCorrect(String mail) {
        return loginuserMapper.queryUserBymail(mail);
    }

    @Override
    public void insertUser(Loginuser loginuser) {
        loginuserMapper.insertUser(loginuser);
    }

    @Override
    public void upDateLock(String mail, int lock, String log) {
        loginuserMapper.updateLock(mail,lock,log);
    }

    @Override
    public void upDateAllLock() {
        loginuserMapper.updateAllLock();
    }

    @Override
    public String sendRegisterMail(String mail) {
        //否则发送注册邮件
        String title="微鱼用户注册验证";
        String randomNum = "";
        for(int i = 0;i<6;i++){
            randomNum+=(int)(Math.random()*10);
        }
        String content = "欢迎您使用微鱼舆情监测系统，本次注册的验证码为"+randomNum+",10分钟内有效。";

        System.out.println("randomNum = "+randomNum);
        MailUtil mailUtil = new MailUtil(mail, title, content);
        new Thread(mailUtil).start();
        return randomNum;
    }

    @Override
    public List<Loginuser> getAllLoginuser() {
        //超级管理员邮箱
        Map<String, String> config = MailUtil.getPropertisFromConfig();
        String from = config.get("from");
        List<Loginuser> loginusers = loginuserMapper.queryAll(from);
        return loginusers;
    }

    //1.普通管理员只能修改普通用户的 topic user blog
    //2.超级管理员可以修改所有 注：如果关闭了topic user blog 那么同时也会失去 admin
    //3.
    @Override
    public boolean updatePower(String mail, String field, boolean status, Loginuser loginuser) {
        String log = getlog(loginuser,field,status);
        boolean flag = true;
        byte num = 0;
        if(status){
            num = 1;
        }
        //获取超级管理员邮箱账号
        Map<String, String> config = MailUtil.getPropertisFromConfig();
        String administer = config.get("from");
        //获取当前管理用户信息
        Loginuser modifyuser = loginuserMapper.queryUserBymail(mail);
        if(administer.equals(loginuser.getMail())){
            //超级管理员
            //打开权限
                //打开管理权限 所有权限全部打开
            //关闭权限
                //关闭普通权限 管理权限关闭
            if(status){
                if(field.equals("admin")){
                    loginuserMapper.updateAllPower(mail);
                }else{
                    loginuserMapper.updatePower(mail,field,num);
                }
            }else{
                loginuserMapper.updatePower(mail,field,num);
                if(!field.equals("admin")){
                    loginuserMapper.updatePower(mail,"admin", (byte) 0);
                }
            }
            //修改log信息
            loginuserMapper.updateLock(mail,modifyuser.getLock(),log);
        }else{
            //普通管理员
            //如果指定用户是管理员 那么没有权力
            //如果要修改管理员权限 没有权力
            //其它的没有了
            if(modifyuser.getAdmin() == 1||field.equals("admin")){
                flag = false;
            }else{
                loginuserMapper.updatePower(mail,field,num);
                //修改log信息
                loginuserMapper.updateLock(mail,modifyuser.getLock(),log);
            }
        }
        return flag;
    }

    @Override
    public boolean deleteUser(String mail, Loginuser loginuser) {
        //获取超级管理员邮箱账号
        Map<String, String> config = MailUtil.getPropertisFromConfig();
        String administer = config.get("from");
        if(loginuser.getMail().equals(administer)){
            loginuserMapper.deleteUser(mail);
            return true;
        }
        return false;
    }

    private String getlog(Loginuser loginuser,String field,boolean status){
        String log = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        log = loginuser.getMail()+"("+loginuser.getName()+")于"+now;
        log+=status ? "打开":"关闭";
        switch (field){
            case "topic":
                log+="你的话题管理权限";
                break;
            case "user":
                log+="你的用户管理权限";
                break;
            case "blog":
                log+="你的微博管理权限";
                break;
            case "admin":
                log+="你的管理员权限";
                break;
        }
        return log;
    }
}
