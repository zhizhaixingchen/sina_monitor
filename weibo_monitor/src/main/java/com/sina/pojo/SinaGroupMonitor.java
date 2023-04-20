package com.sina.pojo;

import java.util.List;

//用户监控分组
public class SinaGroupMonitor {

  private int g_id;                           //组id 主键
  private String g_name;                      //组名称
  private int g_time;                         //刷新时间  秒
  private int g_user_nummax;                  //当前组用户最大容量
  private int g_user_numcur;                  //当前组用户数目
  private List<SinaUserMonitor> userMonitor;  //当前组对应用户

  public SinaGroupMonitor(int g_id, String g_name, int g_time, int g_user_nummax, int g_user_numcur) {
    this.g_id = g_id;
    this.g_name = g_name;
    this.g_time = g_time;
    this.g_user_nummax = g_user_nummax;
    this.g_user_numcur = g_user_numcur;
  }

  public SinaGroupMonitor() {
  }

  public int getG_id() {
    return g_id;
  }

  public void setG_id(int g_id) {
    this.g_id = g_id;
  }

  public String getG_name() {
    return g_name;
  }

  public void setG_name(String g_name) {
    this.g_name = g_name;
  }

  public int getG_time() {
    return g_time;
  }

  public void setG_time(int g_time) {
    this.g_time = g_time;
  }

  public int getG_user_nummax() {
    return g_user_nummax;
  }

  public void setG_user_nummax(int g_user_nummax) {
    this.g_user_nummax = g_user_nummax;
  }

  public int getG_user_numcur() {
    return g_user_numcur;
  }

  public void setG_user_numcur(int g_user_numcur) {
    this.g_user_numcur = g_user_numcur;
  }

  public List<SinaUserMonitor> getUserMonitor() {
    return userMonitor;
  }

  public void setUserMonitor(List<SinaUserMonitor> userMonitor) {
    this.userMonitor = userMonitor;
  }

  @Override
  public String toString() {
    return "SinaGroupMonitor{" +
            "g_id=" + g_id +
            ", g_name='" + g_name + '\'' +
            ", g_time=" + g_time +
            ", g_user_nummax=" + g_user_nummax +
            ", g_user_numcur=" + g_user_numcur +
            ", userMonitor=" + userMonitor +
            '}';
  }
}
