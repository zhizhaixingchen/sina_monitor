package com.sina.pojo;


public class Loginuser {

  private String mail;
  private String name;
  private String password;
  private byte topic;
  private byte user;
  private byte blog;
  private byte admin;
  private int lock;

  public Loginuser() {
  }

  public Loginuser(String mail, String name, String password, byte topic, byte user, byte blog, byte admin, int lock, String log) {
    this.mail = mail;
    this.name = name;
    this.password = password;
    this.topic = topic;
    this.user = user;
    this.blog = blog;
    this.admin = admin;
    this.lock = lock;
    this.log = log;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public byte getTopic() {
    return topic;
  }

  public void setTopic(byte topic) {
    this.topic = topic;
  }

  public byte getUser() {
    return user;
  }

  public void setUser(byte user) {
    this.user = user;
  }

  public byte getBlog() {
    return blog;
  }

  public void setBlog(byte blog) {
    this.blog = blog;
  }

  public byte getAdmin() {
    return admin;
  }

  public void setAdmin(byte admin) {
    this.admin = admin;
  }

  public int getLock() {
    return lock;
  }

  public void setLock(int lock) {
    this.lock = lock;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  private String log;

  @Override
  public String toString() {
    return "Loginuser{" +
            "mail='" + mail + '\'' +
            ", name='" + name + '\'' +
            ", password='" + password + '\'' +
            ", topic=" + topic +
            ", user=" + user +
            ", blog=" + blog +
            ", admin=" + admin +
            ", lock=" + lock +
            ", log='" + log + '\'' +
            '}';
  }
}
