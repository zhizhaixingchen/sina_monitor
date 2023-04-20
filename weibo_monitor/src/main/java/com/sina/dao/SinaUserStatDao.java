package com.sina.dao;

import com.sina.pojo.UserBlogES;

import java.util.List;

public interface SinaUserStatDao {
    //查询数据
        //默认根据时间查询
    List<UserBlogES> queryByField(String field, String rotation, int from, int size);
    //根据姓名查询
    List<UserBlogES> queryByname(String name, int from, int size);
    //根据分组查询
    List<UserBlogES> queryByGroup(List<Long> uidList, int from, int size);
    //批量插入数据
    void insertData(List<UserBlogES> singleBlogList);
    //插入一条
    void insertOne(UserBlogES singleBlog);
}
