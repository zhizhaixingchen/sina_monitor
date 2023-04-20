package com.sina.service;

import com.sina.pojo.SingleBlog;

import java.util.List;

public interface SinaBlogService {
    //根据mid 从互联网获取微博
    SingleBlog getSinaBlogFromInet(String uid,String mid);
    //从elasticsearch获取历史数据
    List<SingleBlog> getSingleBlogFromDB(String mid);

    //批量获取
    List<SingleBlog> getSinaBlogListFromInet(long uid, List<String> currentBlog);
}
