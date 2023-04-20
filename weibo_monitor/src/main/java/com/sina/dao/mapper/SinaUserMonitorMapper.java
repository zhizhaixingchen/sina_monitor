package com.sina.dao.mapper;

import com.sina.pojo.SinaUserMonitor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinaUserMonitorMapper {
    //根据主键返回监控对象
    SinaUserMonitor queryUserMonitorByUid(@Param("u_id") long u_id);
    //返回微博数目
    long queryBlogNumFromDB(@Param("u_id") long u_id);

    //插入userMonitor
    void insertUserMonitor(SinaUserMonitor userMonitor);

    //删除UserMonitor
    void deleteUserMonitor(@Param("u_id") long u_id);

    //修改userMonitor分组
    void updateUserMonitorGroup(@Param("u_id")long u_id, @Param("g_id") long g_id);

    //修改userMonitor微博更新数目
    void updateUserMonitorBlogNum(@Param("u_id")long u_id, @Param("u_blognum") long u_blognum, @Param("u_hot") long hot);

    List<SinaUserMonitor> queryAll();

    List<Long> queryUidByGid(@Param("g_id") int g_id);
}
