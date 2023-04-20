package com.sina.dao;

import com.sina.pojo.ScanTopicBean;

import java.util.List;

public interface SinaTopicScanDao {

    void insertTopic(ScanTopicBean scanTopicBean);
    List<ScanTopicBean> queryFive();
    List<String> queryPhrase(String keyword);
}
