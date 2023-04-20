package com.sina.service;

import com.sina.pojo.User;

import java.util.List;

public interface SinaUserMessageService {
    //使用爬虫查询用户数据
    List<User> searchUserFromInet(String username);
    //显示用户统计信息
    List<User> queryFromES(long uid);
    //更新用户信息，主要更新发表微博数目 和最新发表的微博的uid
    User spiderUserDetail(User u);
}
