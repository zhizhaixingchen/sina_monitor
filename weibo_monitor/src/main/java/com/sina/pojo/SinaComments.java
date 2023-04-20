package com.sina.pojo;

public class SinaComments {
    private String pub_time;
    private String context;
    private double emotion;
    private long like;
    private User user;

    public SinaComments() {
    }

    public SinaComments(String pub_time, String context, double emotion, long like) {
        this.pub_time = pub_time;
        this.context = context;
        this.emotion = emotion;
        this.like = like;

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

    public double getEmotion() {
        return emotion;
    }

    public void setEmotion(double emotion) {
        this.emotion = emotion;
    }

    public long getLike() {
        return like;
    }

    public void setLike(long like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "SinaComments{" +
                "pub_time='" + pub_time + '\'' +
                ", context='" + context + '\'' +
                ", emotion=" + emotion +
                ", like=" + like +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
