package com.sina.service;

import com.sina.pojo.SinaGroupMonitor;

import java.util.List;

public interface SinaUserGroupService {
    //获取所有分组信息
    List<SinaGroupMonitor> getAllGroup();
    //获取可以使用的分组
    List<SinaGroupMonitor> getCanUseGroup();

    void addGroup(String groupname, int freshtime, int maxnum);

    void deleteGroup(int key);

    void freshGroup(int g_id, String field, String value);
}
