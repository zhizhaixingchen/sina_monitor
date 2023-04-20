package com.sina.pojo;

import io.searchbox.annotations.JestId;

import java.util.List;
/*
* 在话题界面     我们展示的信息有
* name      微博名称
* imgUrl    头像
* context   内容
* pub_time  发布时间
* transfer  转发
* comments  评价
* like      点赞数目
*
* 点击之后显示
* hot       热度
* emotion   情感指数
* comments  评论情况
*
* 点击进入微博页面之后显示
* 热度曲线
* 情感指数曲线
* 评论曲线
* */
public class SingleBlog {
    @JestId
    private long current_date;      //微博存储主键
    private long mid;               //当前微博的mid
    private String name;            //发表微博用户
    private String myUrl;           //blog地址
    private String uid;             //发表微博用户uid
    private String imgUrl;          //头像
    private String context;         //内容
    private String pub_time;        //发布时间
    private long transfer;          //转发数目
    private long comments_num;      //评价数量
    private List<SinaComments> commentsList;    //评价详细列表
    private long like;              //点赞
    private long hot;               //微博热度
    private double emotion;         //微博情感指数



    public SingleBlog() {
    }
    //基本 都要使用
    public SingleBlog(long mid, String name, String uid,String imgUrl, String context, String pub_time, long transfer, long comments_num, long like) {
        this.mid = mid;
        this.name = name;
        this.uid = uid;
        this.imgUrl = imgUrl;
        this.context = context;
        this.pub_time = pub_time;
        this.transfer = transfer;
        this.comments_num = comments_num;
        this.like = like;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPub_time() {
        return pub_time;
    }

    public void setPub_time(String pub_time) {
        this.pub_time = pub_time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public long getTransfer() {
        return transfer;
    }

    public void setTransfer(long transfer) {
        this.transfer = transfer;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public long getHot() {
        return hot;
    }

    public void setHot(long hot) {
        this.hot = hot;
    }

    public double getEmotion() {
        return emotion;
    }

    public void setEmotion(double emotion) {
        this.emotion = emotion;
    }

    public long getComments_num() {
        return comments_num;
    }

    public void setComments_num(long comments_num) {
        this.comments_num = comments_num;
    }

    public List<SinaComments> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<SinaComments> commentsList) {
        this.commentsList = commentsList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(long current_date) {
        this.current_date = current_date;
    }

    public String getMyUrl() {
        return myUrl;
    }

    public void setMyUrl(String myUrl) {
        this.myUrl = myUrl;
    }

    @Override
    public String toString() {
        return "SingleBlog{" +
                "current_date=" + current_date +
                ", mid=" + mid +
                ", name='" + name + '\'' +
                ", myUrl='" + myUrl + '\'' +
                ", uid='" + uid + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", context='" + context + '\'' +
                ", pub_time='" + pub_time + '\'' +
                ", transfer=" + transfer +
                ", comments_num=" + comments_num +
                ", commentsList=" + commentsList +
                ", like=" + like +
                ", hot=" + hot +
                ", emotion=" + emotion +
                '}';
    }

}
