package com.sina.dao;

//月度统计
/*
    old_sina		存储至hdfs		清空old_sina
	old_sinahotstat	存储至hdfs		清空old_sinahotstat
	sinatopic		存储至hdfs		清空sinatopic
	blogs		    存储至hdfs		清空blogs
	sinatopicbeanscan				清空sinatopicbeanscan*/
public interface SinaMonthStat {
    //两个热度信息进行存储
    void oldSinaStore();
    void old_sinahotstatStore();
    //监控话题存储
    void sinaTopicStore();
    //监控微博存储
    void blogsStore();

    //初始化微博详情表
    void initOldSina();
    //初始化微博统计信息表
    void initOld_sinahotstat();
    //初始化话题存储表
    void initSinaTopicStore();
    //初始化blog表
    void initBlogs();
    //初始化搜索表
    //不进行清空操作   保存用户监控的话题 不清空   不多
    //void initSinaTopicbeanScan();

}
