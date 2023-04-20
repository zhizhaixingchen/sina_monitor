package com.sina.dao;

import com.sina.pojo.SinaHotTopicStatBean;

import java.util.List;

public interface SinaHotTopicStatDao {
    void insertData(SinaHotTopicStatBean sinaHotTopicStatBean);
    List<SinaHotTopicStatBean> queryAll();
}
