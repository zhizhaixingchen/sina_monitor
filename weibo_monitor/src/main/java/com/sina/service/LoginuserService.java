package com.sina.service;

import com.sina.pojo.Loginuser;

import java.util.List;

public interface LoginuserService {
    //判断账户的正确性
    // return null 用户不存在
    // return Loginuser
    // lock = 0 账户锁定 不能登录           注：这里需要设置个定时器进行恢复锁定
    Loginuser checkLoginCorrect(String mail);

    //插入新用户
    void insertUser(Loginuser loginuser);

    //更新lock次数  用户登录成功后调用
    void upDateLock(String mail,int lock,String log);

    //定时器调用 更新所有用户的次数
    void upDateAllLock();

    //发送注册邮件
    String sendRegisterMail(String mail);

    //获取除管理员之外的所有用户
    List<Loginuser> getAllLoginuser();

    //更新权限
    boolean updatePower(String mail, String field, boolean status, Loginuser loginuser);

    boolean deleteUser(String mail, Loginuser loginuser);
}
