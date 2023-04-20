package com.sina.pojo;
/*
* 话题
* 用途:
*       1.热搜
*
* */

import io.searchbox.annotations.JestId;

import java.util.Date;
import java.util.List;

//存储Top页面的话题数据
public class SinaTopBean {
    @JestId
    private long current_date;          //主键  话题获取时间
    private int stage;                 //当前排名
    private String keyword;             //话题内容
    private long access_count;          //话题关注数目
    private double emotion_point;       //话题情感倾向
    private String link;                //话题超链接
    private List<String> words;         //关键词


    public SinaTopBean() {
    }

    public SinaTopBean(long current_date, int stage, String keyword, long access_count, double emotion_point, String link) {
        this.current_date = current_date;
        this.stage = stage;
        this.keyword = keyword;
        this.access_count = access_count;
        this.emotion_point = emotion_point;
        this.link = link;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getAccess_count() {
        return access_count;
    }

    public void setAccess_count(long access_count) {
        this.access_count = access_count;
    }

    public double getEmotion_point() {
        return emotion_point;
    }

    public void setEmotion_point(double emotion_point) {
        this.emotion_point = emotion_point;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }

    @Override
    public String toString() {
        return "SinaTopBean{" +
                "current_date=" + current_date +
                ", stage=" + stage +
                ", keyword='" + keyword + '\'' +
                ", access_count=" + access_count +
                ", emotion_point=" + emotion_point +
                ", link='" + link + '\'' +
                ", words=" + words +
                '}';
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
