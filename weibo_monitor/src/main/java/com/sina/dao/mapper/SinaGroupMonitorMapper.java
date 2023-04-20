package com.sina.dao.mapper;

import com.sina.pojo.SinaGroupMonitor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinaGroupMonitorMapper {

    //获取所有分组
    List<SinaGroupMonitor> queryAll();
    //查询分组
    SinaGroupMonitor queryGroupByG_name(String g_name);

    SinaGroupMonitor queryGroupById(long g_id);

    //修改curNum
    void modifyCurNum(@Param("curnum") int curnum,@Param("g_id") int g_id);

    //获取可以使用的分组
    List<SinaGroupMonitor> queryNotFull();

    void insertGroup(@Param("groupname") String groupname,@Param("freshtime") int freshtime,@Param("maxnum") int maxnum);

    void deleteGroupById(@Param("key") int key);

    void updateGroup(@Param("g_id") int g_id,@Param("field") String field,@Param("value") String value);
}
