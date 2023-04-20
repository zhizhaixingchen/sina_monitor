package com.sina.service;

import com.sina.pojo.SinaUserMonitor;

import java.util.List;

public interface SinaUserMonitorService {
    //获取用户监控信息
    SinaUserMonitor getUserMonitor(long uid);
    //获取用户发表微博数目
    long getUserBlogNum(long uid);
    //新建用户监控信息
    SinaUserMonitor createUserMonitor(SinaUserMonitor userMonitor,int g_id);
    //删除用户监控信息
    SinaUserMonitor deleteUserMonitor(SinaUserMonitor userMonitor);
    //修改用户监控信息  修改分组
    SinaUserMonitor modifyGroupUserMonitor(SinaUserMonitor userMonitor,int g_id);
    //修改用户监控信息  修改微博数目和热度     两者一块修改
    void modifyUserMonitorBlogNum(long uid, long blognum, long hot);

    //获取所有监控信息
    List<SinaUserMonitor> getAll();

}
