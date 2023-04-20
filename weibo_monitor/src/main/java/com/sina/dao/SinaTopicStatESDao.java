package com.sina.dao;

import com.sina.pojo.SinaTopicESStatBean;

import java.util.List;

public interface SinaTopicStatESDao {
    //查询所有统计信息
    List<SinaTopicESStatBean> queryAll(String key,String type);
    //根据关键字查询统计信息
    SinaTopicESStatBean queryByKeyword(String keyword);
}
