package com.sina.pojo;
//用于微博监控 在musql中记录监控顺序
public class BlogMysql{
    private long mid;
    private long uid;
    private int time;
    private String name;
    private long starttime;
    private double emotion;
//    0代表未监控  1代表监控
    private byte ismon;

    public BlogMysql() {
    }

    public BlogMysql(long mid, long uid, int time, String name, long starttime, double emotion, byte ismon) {
        this.mid = mid;
        this.uid = uid;
        this.time = time;
        this.name = name;
        this.starttime = starttime;
        this.emotion = emotion;
        this.ismon = ismon;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    @Override
    public String toString() {
        return "BlogMysql{" +
                "mid=" + mid +
                ", uid=" + uid +
                ", time=" + time +
                ", name='" + name + '\'' +
                ", starttime=" + starttime +
                ", emotion=" + emotion +
                ", ismon=" + ismon +
                '}';
    }

    public double getEmotion() {
        return emotion;
    }

    public void setEmotion(double emotion) {
        this.emotion = emotion;
    }

    public byte getIsmon() {
        return ismon;
    }

    public void setIsmon(byte ismon) {
        this.ismon = ismon;
    }
}
