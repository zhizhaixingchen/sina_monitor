package com.sina.pojo;

import io.searchbox.annotations.JestId;

import java.util.Date;

//来自mysql的话题统计数据
public class SinaHotTopicStatBean {
    @JestId
    private long current_date;
    private long hot;
    private Double emotion;


    public SinaHotTopicStatBean() {
    }

    public SinaHotTopicStatBean(long current_date, long hot, double emotion) {
        this.current_date = current_date;
        this.hot = hot;
        this.emotion = emotion;
    }

    public long getHot() {
        return hot;
    }

    public void setHot(long hot) {
        this.hot = hot;
    }

    public Double getEmotion() {
        return emotion;
    }

    public void setEmotion(Double emotion) {
        this.emotion = emotion;
    }

    @Override
    public String toString() {
        return "SinaHotTopicStatBean{" +
                "current_date=" + current_date +
                ", hot=" + hot +
                ", emotion=" + emotion +
                '}';
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }
}
