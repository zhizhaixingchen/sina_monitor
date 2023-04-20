package com.sina.service;

import com.sina.pojo.ScanTopicBean;

import java.util.List;

public interface SinaTopicScanService {

    void addTopicScan(String topicName, boolean isMon);
    List<String> getHistory();
    List<String> searchImagine(String keyword);

}
