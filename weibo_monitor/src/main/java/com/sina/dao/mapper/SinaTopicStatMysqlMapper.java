package com.sina.dao.mapper;

import com.sina.pojo.SinaHotTopicStatBean;
import org.springframework.stereotype.Repository;

import java.util.List;

//废弃 数据转存至es
@Repository
public interface SinaTopicStatMysqlMapper {

    void insertData(SinaHotTopicStatBean sinaHotTopicStatBean);
    List<SinaHotTopicStatBean> queryAll();
}
