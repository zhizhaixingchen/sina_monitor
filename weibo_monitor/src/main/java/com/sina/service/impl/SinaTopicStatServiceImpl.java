package com.sina.service.impl;

import com.sina.dao.SinaHotTopicStatDao;
import com.sina.dao.SinaTopicStatESDao;
import com.sina.pojo.SinaTopicESStatBean;
import com.sina.pojo.SinaHotTopicStatBean;
import com.sina.service.SinaTopicStatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SinaTopicStatServiceImpl implements SinaTopicStatService {
    private SinaTopicStatESDao esDao;
    private SinaHotTopicStatDao topicStatDao;
    public SinaTopicStatServiceImpl(SinaTopicStatESDao esDao, SinaHotTopicStatDao topicStatDao) {
        this.esDao = esDao;
        this.topicStatDao = topicStatDao;
    }


    @Override
    public List<SinaTopicESStatBean> esFindAll(String key,String type) {
        return esDao.queryAll(key,type);
    }

    @Override
    public SinaTopicESStatBean esFindByKeyword(String keyword) {
        return esDao.queryByKeyword(keyword);
    }

    //在插入ES index--> /sina/hot时插入该数据
    @Override
    public void insertOne(SinaHotTopicStatBean bean) {
        topicStatDao.insertData(bean);
    }

    @Override
    public List<SinaHotTopicStatBean> FindAllstat() {
        return topicStatDao.queryAll();
    }
}
