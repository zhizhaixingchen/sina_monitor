package com.sina.service.impl;

import com.sina.dao.SinaTopicMonitorDao;
import com.sina.dao.SinaTopicScanDao;
import com.sina.pojo.ScanTopicBean;
import com.sina.service.SinaTopicMonitorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SinaTopicMonitorServiceImpl implements SinaTopicMonitorService {

    private SinaTopicMonitorDao topicMonitorDao;
    private SinaTopicScanDao sinaTopicScanDao;

    public SinaTopicMonitorServiceImpl(SinaTopicMonitorDao topicMonitorDao, SinaTopicScanDao sinaTopicScanDao) {
        this.topicMonitorDao = topicMonitorDao;
        this.sinaTopicScanDao = sinaTopicScanDao;
    }

    //更新TopicScan 需要获取更新后的数据    话题监控界面使用
    @Override
    public List<ScanTopicBean> updateTopicScan(String topicName, boolean isMon, List<ScanTopicBean> list) {
        //将空格替换为逗号，主键中不能存在空格
        topicName = topicName.replace(" ",",");
        ScanTopicBean scanTopicBean = new ScanTopicBean(topicName,isMon,System.currentTimeMillis());
        sinaTopicScanDao.insertTopic(scanTopicBean);

        //由于es读写延迟,更新数据不能重新从数据库中获取值
        for (ScanTopicBean topicBean : list) {
            if(topicBean.getTopicName().equals(topicName)){
                topicBean.setMonitor(isMon);
                topicBean.setCurrent_date(System.currentTimeMillis());
            }
        }
        return list;
    }

    //根据内容获取监控结果
    @Override
    public List<ScanTopicBean> searchByContent(String content) {
        return topicMonitorDao.queryByContent(content);
    }

    //根据时间获取监控结果
    @Override
    public List<ScanTopicBean> getAllScan(long beginTime,long endTime) {
        //通过分页获取所有
        int page = 0;
        List<ScanTopicBean> allList = new ArrayList<>();
        List<ScanTopicBean> scanTopicBeanList = topicMonitorDao.queryByTime(beginTime,endTime,10000*page++);
        allList.addAll(scanTopicBeanList);
        while(scanTopicBeanList.size()==10000){
            scanTopicBeanList = topicMonitorDao.queryByTime(beginTime,endTime,10000*page++);
            allList.addAll(scanTopicBeanList);
        }
        return allList;
    }
}
