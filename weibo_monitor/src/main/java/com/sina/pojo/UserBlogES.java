package com.sina.pojo;

import io.searchbox.annotations.JestId;

//ES存储     不存储SingleBlog    因为我们需要的是用户今天发了哪些微博 不需要具体那一篇微博的变化情况
public class UserBlogES {
    @JestId
    private long current_date;
    private String name;            //根据名称快速查询
    private long uid;
    private long mid;
    private double emotion;         //舆情排序
    public UserBlogES() {
    }

    public UserBlogES(long current_date, String name, long uid, long mid, double emotion) {
        this.current_date = current_date;
        this.name = name;
        this.uid = uid;
        this.mid = mid;
        this.emotion = emotion;
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }

    public long getUid() {
        return uid;
    }


    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserBlogES{" +
                "current_date=" + current_date +
                ", name='" + name + '\'' +
                ", uid=" + uid +
                ", mid=" + mid +
                '}';
    }

    public double getEmotion() {
        return emotion;
    }

    public void setEmotion(double emotion) {
        this.emotion = emotion;
    }
}
