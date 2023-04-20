package com.sina.pojo;

import io.searchbox.annotations.JestId;

import java.util.List;


//存储一个话题的详细信息
//主要是关于话题热搜
public class SinaTopicBean {
    @JestId
    private long current_date;                  //当前时间
    private String imgUrl;                      //图片
    private SinaTopBean sinaTopBean;            //话题热搜情况（是否上热搜）
    private String readNum;                     //当前阅读数量
    private String discuss;                     //当前讨论数量
    private String keyword;                     //话题名称，与SinaTopBean中的keyword重复
    private double finalEmotionStage;           //最终情感评分
    private List<SingleBlog> hot_blog;          //当前话题下面的热门微博，若没有热门微博则不显示

    public SinaTopicBean() {
    }

    //不包含话题热搜情况   当前话题下热门微博
    public SinaTopicBean(long current_date, String readNum, String discuss, String keyword,double finalEmotionStage) {
        this.current_date = current_date;
        this.readNum = readNum;
        this.discuss = discuss;
        this.keyword = keyword;
        this.finalEmotionStage = finalEmotionStage;
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }

    public SinaTopBean getSinaTopBean() {
        return sinaTopBean;
    }

    public void setSinaTopBean(SinaTopBean sinaTopBean) {
        this.sinaTopBean = sinaTopBean;
    }

    public String getReadNum() {
        return readNum;
    }

    public void setReadNum(String readNum) {
        this.readNum = readNum;
    }

    public String getDiscuss() {
        return discuss;
    }

    public void setDiscuss(String discuss) {
        this.discuss = discuss;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<SingleBlog> getHot_blog() {
        return hot_blog;
    }

    public void setHot_blog(List<SingleBlog> hot_blog) {
        this.hot_blog = hot_blog;
    }

    public double getFinalEmotionStage() {
        return finalEmotionStage;
    }

    public void setFinalEmotionStage(double finalEmotionStage) {
        this.finalEmotionStage = finalEmotionStage;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


    @Override
    public String toString() {
        return "SinaTopicBean{" +
                "current_date=" + current_date +
                ", sinaTopBean=" + sinaTopBean +
                ", readNum='" + readNum + '\'' +
                ", discuss='" + discuss + '\'' +
                ", keyword='" + keyword + '\'' +
                ", finalEmotionStage=" + finalEmotionStage +
                ", hot_blog=" + hot_blog +
                '}';
    }

}
