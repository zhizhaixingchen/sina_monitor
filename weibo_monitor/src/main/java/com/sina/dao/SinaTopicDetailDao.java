package com.sina.dao;

import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaTopicBean;

import java.util.List;

public interface SinaTopicDetailDao {
    //存储sinaTopicBean
    public void storeSinaTopic(SinaTopicBean topicBean);
    //查询sinaTopicBean 数据库
    public List<SinaTopicBean> getSinaTopicBeanFromDB(String keyword,int from);

}
