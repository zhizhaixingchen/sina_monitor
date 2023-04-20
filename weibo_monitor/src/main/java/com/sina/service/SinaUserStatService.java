package com.sina.service;

import com.sina.pojo.UserBlogES;
import com.sina.pojo.SingleBlog;

import java.util.List;

public interface SinaUserStatService {
    //获取最新20条信息
    List<SingleBlog> queryDefault(int from,int size);
    //获取指定分组信息      根据g_id查询u_id mysql
    List<SingleBlog> queryBlogByGroup(int g_id,int from,int size);
    //查询指定用户信息
    List<SingleBlog> queryBlogByUser(String name,int from,int size);
    //根据指定字段进行升序降序
    List<SingleBlog> queryBlogByEmotion(String rotation,int from,int size);
    //插入信息
    void insertData(List<UserBlogES> userBlogESList);
    void insertData(UserBlogES userBlogES);
}
