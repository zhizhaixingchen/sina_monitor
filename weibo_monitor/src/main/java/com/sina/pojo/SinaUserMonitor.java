package com.sina.pojo;
import java.sql.Date;

public class SinaUserMonitor {

  private long u_id;
  private String u_name;
  private long u_hot;
  private long u_blognum;
  private SinaGroupMonitor groupMonitor;

  //新增信息
  private String u_location;              //在获取基本信息那里可以获取的一些信息    location
  private String u_host;                  //个人主页
  private String u_imgUrl;                //头像

  public SinaUserMonitor(long u_id, String u_name, long u_hot, long u_blognum, SinaGroupMonitor groupMonitor, String u_location, String u_host, String u_imgUrl) {
    this.u_id = u_id;
    this.u_name = u_name;
    this.u_hot = u_hot;
    this.u_blognum = u_blognum;
    this.groupMonitor = groupMonitor;
    this.u_location = u_location;
    this.u_host = u_host;
    this.u_imgUrl = u_imgUrl;
  }

  public SinaUserMonitor() {
  }

  public long getU_id() {
    return u_id;
  }

  public void setU_id(long u_id) {
    this.u_id = u_id;
  }

  public String getU_name() {
    return u_name;
  }

  public void setU_name(String u_name) {
    this.u_name = u_name;
  }

  public long getU_hot() {
    return u_hot;
  }

  public void setU_hot(long u_hot) {
    this.u_hot = u_hot;
  }

  public long getU_blognum() {
    return u_blognum;
  }

  public void setU_blognum(long u_blognum) {
    this.u_blognum = u_blognum;
  }

  public SinaGroupMonitor getGroupMonitor() {
    return groupMonitor;
  }

  public void setGroupMonitor(SinaGroupMonitor groupMonitor) {
    this.groupMonitor = groupMonitor;
  }


  public String getU_location() {
    return u_location;
  }

  public void setU_location(String u_location) {
    this.u_location = u_location;
  }

  public String getU_host() {
    return u_host;
  }

  public void setU_host(String u_host) {
    this.u_host = u_host;
  }

  public String getU_imgUrl() {
    return u_imgUrl;
  }

  public void setU_imgUrl(String u_imgUrl) {
    this.u_imgUrl = u_imgUrl;
  }


  @Override
  public String toString() {
    return "SinaUserMonitor{" +
            "u_id=" + u_id +
            ", u_name='" + u_name + '\'' +
            ", u_hot=" + u_hot +
            ", u_blognum=" + u_blognum +
            ", groupMonitor=" + groupMonitor +
            ", location='" + u_location + '\'' +
            ", host='" + u_host + '\'' +
            ", imgUrl='" + u_imgUrl + '\'' +
            '}';
  }
}
