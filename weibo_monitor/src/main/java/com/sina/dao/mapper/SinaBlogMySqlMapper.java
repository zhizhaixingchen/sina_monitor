package com.sina.dao.mapper;

import com.sina.pojo.BlogMysql;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinaBlogMySqlMapper {

    //查询所有监控微博
    List<BlogMysql> queryAll(@Param("from") int from,@Param("size") int size);

    //获取指定用户的微博
    List<BlogMysql> queryByUser(@Param("name") String name,@Param("from") int from,@Param("size") int size);

    //根据时间获取微博
    List<BlogMysql> queryByTime(@Param("start")long starttime,@Param("end")long endtime,@Param("from") int from,@Param("size") int size);

    //通过情感值对微博进行升序与降序排列
    List<BlogMysql> queryByEmotion(@Param("rotation") String rotation,@Param("from") int from,@Param("size") int size);

    //获取当前正在监控的微博
    List<BlogMysql> queryByMonitor(@Param("from") int from,@Param("size") int size);
    //修改微博监控时间
    void modifyTime(@Param("time") int time,@Param("mid") long mid);

    //追加
    void addBlog(BlogMysql blogMysql);
    //修改监控状态
    void changeMonState(@Param("ismon")byte ismon,@Param("mid")long mid);

    //删除  暂时不用 添加监控 我们覆盖    删除监控 我们修改监控值
    void deleteBlog(@Param("mid") long mid);

    BlogMysql queryByMid(@Param("mid")long mid);


}
