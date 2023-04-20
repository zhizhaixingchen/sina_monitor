package com.sina.service.impl;

import com.sina.dao.SinaDayStat;
import com.sina.dao.SinaMonthStat;
import com.sina.service.SinaDMStatService;
import org.springframework.stereotype.Service;

@Service
public class SinaDMStatServiceImpl implements SinaDMStatService {
    private SinaDayStat sinaDayStat;
    private SinaMonthStat sinaMonthStat;
    public SinaDMStatServiceImpl(SinaDayStat sinaDayStat, SinaMonthStat sinaMonthStat) {
        this.sinaDayStat = sinaDayStat;
        this.sinaMonthStat = sinaMonthStat;
    }

    @Override
    public void dayWork() {
        //热门详细信息转移
        sinaDayStat.sinaHotTransfer();
        //今日热门详细信息数据清空
        sinaDayStat.sinaHotclear();
        //热门统计信息转移
        sinaDayStat.sinaHotStatTransfer();
        //今日热门统计信息数据清空
        sinaDayStat.sinaHotStatClear();
        //用户数据清空
        sinaDayStat.usersClear();
    }

    @Override
    public void monthWork() {
        //两个热度信息进行存储
        sinaMonthStat.oldSinaStore();
        sinaMonthStat.old_sinahotstatStore();
        //监控话题存储
        sinaMonthStat.sinaTopicStore();
        //监控微博存储
        sinaMonthStat.blogsStore();

        //初始化微博详情表
        sinaMonthStat.initOldSina();
        //初始化微博统计信息表
        sinaMonthStat.initOld_sinahotstat();
        //初始化话题存储表
        sinaMonthStat.initSinaTopicStore();
        //初始化blog表
        sinaMonthStat.initBlogs();

        //初始化搜索表
        //sinaMonthStat.initSinaTopicbeanScan();
    }
}
