package com.sina.pojo;

import io.searchbox.annotations.JestId;

import java.util.List;

public class User {
/*    @JestId
    private long current_date;                  //elastic 主键*/
    private long uid;                           //用户id
    private String username;                    //用户名
    private String imgUrl;                      //用户头像
    private String host;                        //用户主页
    private String location;                    //用户地址
    private List<String> currentBlog;           //用户发表微博的mid
    private String blogNum;                     //用户发表微博数目
    private String focus;                       //用户关注人数
    private String fans;                        //用户粉丝
    private String description;                 //用户基本描述信息

    public User() {
    }

    public User(long uid, String username, String imgUrl, String host, String location, String blogNum, String focus, String fans) {
        this.uid = uid;
        this.username = username;
        this.imgUrl = imgUrl;
        this.host = host;
        this.blogNum = blogNum;
        this.focus = focus;
        this.fans = fans;
        this.location = location;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getCurrentBlog() {
        return currentBlog;
    }

    public void setCurrentBlog(List<String> currentBlog) {
        this.currentBlog = currentBlog;
    }

    public String getBlogNum() {
        return blogNum;
    }

    public void setBlogNum(String blogNum) {
        this.blogNum = blogNum;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
/*
    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }*/

    @Override
    public String toString() {
        return "User{" +
//                "current_date=" + current_date +
                " uid=" + uid +
                ", username='" + username + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", host='" + host + '\'' +
                ", location='" + location + '\'' +
                ", currentBlog=" + currentBlog +
                ", blogNum='" + blogNum + '\'' +
                ", focus='" + focus + '\'' +
                ", fans='" + fans + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
