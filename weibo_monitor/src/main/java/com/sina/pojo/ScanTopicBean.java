package com.sina.pojo;

import io.searchbox.annotations.JestId;

import java.util.Date;

public class ScanTopicBean {
    @JestId
    private String topicName;
    //该话题是否被监控
    private boolean monitor;
    //追加时间进行排序
    private long current_date;

    public ScanTopicBean() {
    }

    //es存储使用
    public ScanTopicBean(String topicName, boolean monitor,long current_date) {
        this.topicName = topicName;
        this.monitor = monitor;
        this.current_date = current_date;
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public boolean isMonitor() {
        return monitor;
    }

    public void setMonitor(boolean monitor) {
        this.monitor = monitor;
    }

    @Override
    public String toString() {
        return "ScanTopicBean{" +
                "current_date=" + current_date +
                ", topicName='" + topicName + '\'' +
                ", monitor=" + monitor +
                '}';
    }
}
