package com.sina.service;

import com.sina.pojo.ScanTopicBean;

import java.util.List;

public interface SinaTopicMonitorService {

    List<ScanTopicBean> getAllScan(long beginTime, long endTime);

    List<ScanTopicBean> updateTopicScan(String topicName, boolean isMon, List<ScanTopicBean> list);

    List<ScanTopicBean> searchByContent(String content);
}
