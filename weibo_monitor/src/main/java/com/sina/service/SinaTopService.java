package com.sina.service;

import com.sina.pojo.SinaTopBean;

import java.util.List;


public interface SinaTopService {
    //获取最新热搜榜       返回当前sinaTop
    List<SinaTopBean> getSinaTopFromInet();
    //根据热搜内容获取指定历史热搜榜
    List<SinaTopBean> getSinaTopFromDB(String content);


    void test();
}
