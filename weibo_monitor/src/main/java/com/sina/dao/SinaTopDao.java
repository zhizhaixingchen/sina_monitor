package com.sina.dao;

import com.sina.pojo.SinaTopBean;

import java.util.List;


public interface SinaTopDao {

    //获取指定热搜榜历史信息
    List<SinaTopBean> getHotFromDB(String content, int page);
    //存储热搜榜信息
    void storeCurrentHotData(List<SinaTopBean> list);
}
