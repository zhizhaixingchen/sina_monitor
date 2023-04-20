package com.sina.service;

import com.sina.pojo.BlogMysql;
import com.sina.pojo.MonitorBlogES;

import java.util.List;
import java.util.Map;

public interface SinaBlogMonitorService {
    //追加监控
    void addMonitor(BlogMysql blogMysql);
    //删除监控
    void deleteMonitor(String mid);
    //修改监控
    void modifyMonitor(BlogMysql blogMysql);
    //获取所有监控微博 默认根据时间
    Map<String,Object> getAllMonitorBlog(int from, int size);
    //根据名字进行筛选
    Map<String,Object> getMonitorBlogByName(String name,int from,int size);
    //根据时间段进行筛选
    Map<String,Object> getMonitorBlogByTime(long start,long end,int from,int size);
    //根据情感值进行筛选
    Map<String,Object> getMonitorBlogByEmotion(String rotation,int from,int size);
    //获取当前正在监控的微博
    Map<String,Object> getMonitorBlogByMonitor(int from,int size);
    //获取当前微博统计信息
    List<MonitorBlogES> getMonitorBlogData(long mid);

    //存储当前微薄统计信息
    void insertMonitorBlogData(MonitorBlogES monitorBlogES);

    BlogMysql getBlogMonitorByMid(String mid);
}
