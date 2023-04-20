package com.sina.dao;

import com.sina.pojo.ScanTopicBean;

import java.util.List;

public interface SinaTopicMonitorDao {
    List<ScanTopicBean> queryByTime(long beginTime, long endTime, int page);
    List<ScanTopicBean> queryByContent(String content);
}
