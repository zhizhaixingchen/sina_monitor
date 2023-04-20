package com.sina.pojo;

//监控微博对应es
public class MonitorBlogES {
    private long current_date;
    private long uid;
    private long mid;
    private long like;
    private long transfer;
    private long comments;
    private double totalEmotion;            //总情感   文本情感60%+评论情感40%

    public MonitorBlogES() {
    }

    public MonitorBlogES(long current_date, long uid, long mid, long like, long transfer, long comments, double totalEmotion) {
        this.current_date = current_date;
        this.uid = uid;
        this.mid = mid;
        this.like = like;
        this.transfer = transfer;
        this.comments = comments;
        this.totalEmotion = totalEmotion;
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

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    public long getTransfer() {
        return transfer;
    }

    public void setTransfer(long transfer) {
        this.transfer = transfer;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public double getTotalEmotion() {
        return totalEmotion;
    }

    public void setTotalEmotion(double totalEmotion) {
        this.totalEmotion = totalEmotion;
    }

    @Override
    public String toString() {
        return "MonitorBlogES{" +
                "current_date=" + current_date +
                ", uid=" + uid +
                ", mid=" + mid +
                ", like=" + like +
                ", transfer=" + transfer +
                ", comments=" + comments +
                ", totalEmotion=" + totalEmotion +
                '}';
    }
}
