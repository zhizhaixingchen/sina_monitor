package com.sina.service;

import com.sina.pojo.SinaTopicESStatBean;
import com.sina.pojo.SinaHotTopicStatBean;

import java.util.List;

public interface SinaTopicStatService {

    //ES
    List<SinaTopicESStatBean> esFindAll(String key,String type);
    SinaTopicESStatBean esFindByKeyword(String keyword);
    //mysql
        //插入一条数据
    void insertOne(SinaHotTopicStatBean bean);
    List<SinaHotTopicStatBean> FindAllstat();
}
