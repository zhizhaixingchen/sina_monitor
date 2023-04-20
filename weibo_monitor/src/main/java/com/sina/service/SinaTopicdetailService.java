package com.sina.service;

import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaTopicBean;

import java.util.List;

public interface SinaTopicdetailService {
    //从网站根据热搜名称获取热搜
    SinaTopicBean getSinaTopicByKeyWordFromInet(String keyWord, SinaTopBean sinaTopBean);

    //从数据库根据热搜内容获取热搜
    List<SinaTopicBean> getSinaTopicByKeywordFromDB(String keyword);

    void test();

    boolean addMonitor(String topicName);

    boolean removeMonitor(String topicName);
}
