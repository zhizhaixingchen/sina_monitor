package com.sina.dao;

import com.sina.pojo.MonitorBlogES;

import java.util.List;

public interface SinaMonitorBlogDao {
    //获取数据
    List<MonitorBlogES> queryByMid(long mid,int page);
    //存储数据
    void insertData(MonitorBlogES monitorBlogES);
}
