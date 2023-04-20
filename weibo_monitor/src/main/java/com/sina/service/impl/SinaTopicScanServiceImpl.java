package com.sina.service.impl;

import com.sina.dao.SinaTopicScanDao;
import com.sina.pojo.ScanTopicBean;
import com.sina.service.SinaTopicScanService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class SinaTopicScanServiceImpl implements SinaTopicScanService {

    private final SinaTopicScanDao sinaTopicScanDao;
    public SinaTopicScanServiceImpl(SinaTopicScanDao sinaTopicScanDao) {
        this.sinaTopicScanDao = sinaTopicScanDao;
    }

    //追加TopicScan 不需要获取更新后的数据   话题详情页使用
    @Override
    public void addTopicScan(String topicName, boolean isMon) {
        //将空格替换为逗号，主键中不能存在空格
        topicName = topicName.replace(" ",",");
        ScanTopicBean scanTopicBean = new ScanTopicBean(topicName,isMon,System.currentTimeMillis());
        sinaTopicScanDao.insertTopic(scanTopicBean);
    }



    //获取最新的5条监控数据
    @Override
    public List<String> getHistory() {
        List<String> list = new ArrayList<>();
        List<ScanTopicBean> scanTopicBeanList = sinaTopicScanDao.queryFive();
        for (ScanTopicBean scanTopicBean : scanTopicBeanList) {
            list.add(scanTopicBean.getTopicName());
        }
        return list;
    }

    @Override
    public List<String> searchImagine(String keyword) {
        return sinaTopicScanDao.queryPhrase(keyword);
    }

}
