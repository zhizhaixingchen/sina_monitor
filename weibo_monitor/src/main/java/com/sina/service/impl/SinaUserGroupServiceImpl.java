package com.sina.service.impl;

import com.sina.dao.mapper.SinaGroupMonitorMapper;
import com.sina.pojo.SinaGroupMonitor;
import com.sina.service.SinaUserGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SinaUserGroupServiceImpl implements SinaUserGroupService {

    private SinaGroupMonitorMapper groupMonitorMapper;
    public SinaUserGroupServiceImpl(SinaGroupMonitorMapper groupMonitorMapper) {
        this.groupMonitorMapper = groupMonitorMapper;
    }

    @Override
    public List<SinaGroupMonitor> getAllGroup() {
        return groupMonitorMapper.queryAll();
    }

    @Override
    public List<SinaGroupMonitor> getCanUseGroup() {
        return groupMonitorMapper.queryNotFull();
    }


    @Override
    public void addGroup(String groupname, int freshtime, int maxnum) {
        groupMonitorMapper.insertGroup(groupname,freshtime,maxnum);
    }

    @Override
    public void deleteGroup(int key) {
        groupMonitorMapper.deleteGroupById(key);
    }

    @Override
    public void freshGroup(int g_id, String field, String value) {
        groupMonitorMapper.updateGroup(g_id,field,value);
    }
}
