package com.sina.service.impl;

import com.sina.dao.SinaMonitorBlogDao;
import com.sina.dao.mapper.SinaBlogMySqlMapper;
import com.sina.pojo.BlogMysql;
import com.sina.pojo.MonitorBlogES;
import com.sina.pojo.SingleBlog;
import com.sina.quartzJob.BlogJob;
import com.sina.service.SinaBlogMonitorService;
import com.sina.service.SinaBlogService;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SinaBlogMonitorServiceImpl implements SinaBlogMonitorService {

    private SinaBlogMySqlMapper blogMySqlMapper;
    private Scheduler scheduler;
    private SinaBlogService blogService;
    private SinaMonitorBlogDao monitorBlogDao;
    public SinaBlogMonitorServiceImpl(SinaBlogMySqlMapper blogMySqlMapper, Scheduler scheduler, SinaBlogService blogService, SinaMonitorBlogDao monitorBlogDao) {
        this.blogMySqlMapper = blogMySqlMapper;
        this.scheduler = scheduler;
        this.blogService = blogService;
        this.monitorBlogDao = monitorBlogDao;
    }

    //监控状态修改
    @Override
    public void addMonitor(BlogMysql blogMysql) {
        //追加定时器
        JobDetail jobDetail = JobBuilder
                .newJob(BlogJob.class)
                .withIdentity(new JobKey(String.valueOf(blogMysql.getMid()), "blogMonitor"))
                .usingJobData("mid", blogMysql.getMid())
                .usingJobData("uid", blogMysql.getUid())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(String.valueOf(blogMysql.getMid()), "blogMonitor")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(blogMysql.getTime()*60).repeatForever())
                .build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        }catch (SchedulerException e) {
                e.printStackTrace();
            }
        //向表中添加数据
        blogMySqlMapper.addBlog(blogMysql);
    }

    @Override
    public void deleteMonitor(String mid) {
        //删除监控
        try {
            boolean exists = scheduler.checkExists(new JobKey(mid, "blogMonitor"));
            if(exists){
                scheduler.deleteJob(new JobKey(mid, "blogMonitor"));
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        //修改监控状态                    0代表未监控  1代表监控
        blogMySqlMapper.changeMonState((byte)0,Long.parseLong(mid));
    }

    //修改主要是修改监控时间   未使用
    @Override
    public void modifyMonitor(BlogMysql blogMysql) {
        try {
            boolean exists = scheduler.checkExists(new JobKey(String.valueOf(blogMysql.getMid()), "blogMonitor"));
            if(exists){
                scheduler.deleteJob(new JobKey(String.valueOf(blogMysql.getMid()), "blogMonitor"));
            }
            JobDetail jobDetail = JobBuilder
                    .newJob(BlogJob.class)
                    .withIdentity(new JobKey(String.valueOf(blogMysql.getMid()), "blogMonitor"))
                    .usingJobData("mid",blogMysql.getMid())
                    .usingJobData("uid",blogMysql.getUid())
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(blogMysql.getMid()), "blogMonitor")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(blogMysql.getTime()).repeatForever())
                    .build();

            scheduler.scheduleJob(jobDetail,trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        //修改监控时间
        blogMySqlMapper.modifyTime(blogMysql.getTime(),blogMysql.getMid());
    }

    //概览界面多条查询

    //默认
    @Override
    public Map<String, Object> getAllMonitorBlog(int from, int size) {
        List<BlogMysql> blogMysqlList = blogMySqlMapper.queryAll(from,size);
        Map<String, Object> map = getMapByBlogMysql(blogMysqlList);
        return map;
    }

    @Override
    public Map<String,Object> getMonitorBlogByName(String name,int from,int size) {
        List<BlogMysql> blogMysqlList = blogMySqlMapper.queryByUser(name,from,size);
        Map<String, Object> map = getMapByBlogMysql(blogMysqlList);
        return map;
    }

    @Override
    public Map<String, Object> getMonitorBlogByTime(long start, long end, int from, int size) {
        List<BlogMysql> blogMysqlList = blogMySqlMapper.queryByTime(start, end, from, size);
        Map<String, Object> map = getMapByBlogMysql(blogMysqlList);
        return map;
    }

    @Override
    public Map<String, Object> getMonitorBlogByEmotion(String rotation, int from, int size) {
        List<BlogMysql> blogMysqlList = blogMySqlMapper.queryByEmotion(rotation,from,size);
        Map<String, Object> map = getMapByBlogMysql(blogMysqlList);
        return map;
    }

    @Override
    public Map<String, Object> getMonitorBlogByMonitor(int from,int size) {
        List<BlogMysql> blogMysqlList = blogMySqlMapper.queryByMonitor(from,size);
        Map<String, Object> map = getMapByBlogMysql(blogMysqlList);
        return map;
    }

    //查询当前正在监控的微博



    //单条查询

    //根据mid获取监控数据
    @Override
    public List<MonitorBlogES> getMonitorBlogData(long mid) {
        int page = 0;
        List<MonitorBlogES> finalMonitorBlog = new ArrayList<>();
        List<MonitorBlogES> monitorBlogES = monitorBlogDao.queryByMid(mid, page++);
        finalMonitorBlog.addAll(monitorBlogES);
        while(monitorBlogES.size() == 10000){
            monitorBlogES = monitorBlogDao.queryByMid(mid, page++);
            finalMonitorBlog.addAll(monitorBlogES);
        }
        return finalMonitorBlog;
    }

    //根据mid获取监控参数
    @Override
    public BlogMysql getBlogMonitorByMid(String mid) {
        return blogMySqlMapper.queryByMid(Long.parseLong(mid));
    }

    //插入数据
    @Override
    public void insertMonitorBlogData(MonitorBlogES monitorBlogES) {
        monitorBlogDao.insertData(monitorBlogES);
    }

    //将BlogMysqlList和SingleBlogList封装在一个对象中，返回给controller
    private Map<String, Object> getMapByBlogMysql(List<BlogMysql> blogMysqlList) {
        Map<String,Object> map = new HashMap<>();
        List<SingleBlog> singleBlogList = new ArrayList<>();
        for (BlogMysql blogMysql : blogMysqlList) {
            SingleBlog singleBlog = blogService.getSinaBlogFromInet(String.valueOf(blogMysql.getUid()), String.valueOf(blogMysql.getMid()));
            singleBlogList.add(singleBlog);
        }
        map.put("singleBlogList",singleBlogList);
        map.put("blogMysqlList",blogMysqlList);
        return map;
    }
}
