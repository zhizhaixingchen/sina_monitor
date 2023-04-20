package com.sina.pojo;

import java.util.List;

//今日统计数据    数据来自ES对/sina/hot进行分组聚合得到
public class SinaTopicESStatBean {
    private String keyword;         //话题名称
    private long maxHot;            //最大热度
    private long beginTime;         //开始时间
    private long endTime;           //结束时间
    private boolean hot;            //当前是否热门
    private double emotion;         //情感倾向
    private int lowStage;           //最低排名
    private int highStage;          //最高排名
    private List<String> allWords;  //词云

    public SinaTopicESStatBean() {
    }

    public SinaTopicESStatBean(String keyword, long maxHot, long beginTime, long endTime, boolean hot, double emotion, int lowStage, int highStage,List<String> allWords) {
        this.keyword = keyword;
        this.maxHot = maxHot;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.hot = hot;
        this.emotion = emotion;
        this.lowStage = lowStage;
        this.highStage = highStage;
        this.allWords = allWords;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getMaxHot() {
        return maxHot;
    }

    public void setMaxHot(long maxHot) {
        this.maxHot = maxHot;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }

    public double getEmotion() {
        return emotion;
    }

    public void setEmotion(double emotion) {
        this.emotion = emotion;
    }

    public int getLowStage() {
        return lowStage;
    }

    public void setLowStage(int lowStage) {
        this.lowStage = lowStage;
    }

    public int getHighStage() {
        return highStage;
    }

    public void setHighStage(int highStage) {
        this.highStage = highStage;
    }

    @Override
    public String toString() {
        return "SinaTopicESStatBean{" +
                "keyword='" + keyword + '\'' +
                ", maxHot=" + maxHot +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", hot=" + hot +
                ", emotion=" + emotion +
                ", lowStage=" + lowStage +
                ", highStage=" + highStage +
                '}';
    }

    public List<String> getAllWords() {
        return allWords;
    }

    public void setAllWords(List<String> allWords) {
        this.allWords = allWords;
    }
}
