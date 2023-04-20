package com.sina.service.impl;

import com.sina.dao.mapper.SinaGroupMonitorMapper;
import com.sina.dao.mapper.SinaUserMonitorMapper;
import com.sina.pojo.SinaGroupMonitor;
import com.sina.pojo.SinaUserMonitor;
import com.sina.quartzJob.UserJob;
import com.sina.service.SinaUserMonitorService;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.List;

//用户监控service
@Service
public class SinaUserMonitorServiceImpl implements SinaUserMonitorService {

    private SinaUserMonitorMapper userMapper;
    private SinaGroupMonitorMapper groupMapper;
    private Scheduler scheduler;
    public SinaUserMonitorServiceImpl(SinaUserMonitorMapper userMapper, SinaGroupMonitorMapper groupMapper, Scheduler scheduler) {
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.scheduler = scheduler;
    }

    @Override
    public SinaUserMonitor getUserMonitor(long uid) {
        return userMapper.queryUserMonitorByUid(uid);
    }

    @Override
    public long getUserBlogNum(long uid) {
        return userMapper.queryBlogNumFromDB(uid);
    }

    //Controller传过来的UserMonitor不包含GroupMonitor属性,我们为其追加GroupMonitor属性，并返回
    @Override
    public SinaUserMonitor createUserMonitor(SinaUserMonitor userMonitor,int g_id) {
        //根据获取分组信息
        SinaGroupMonitor groupMonitor = groupMapper.queryGroupById(g_id);
        //更新分组 当前分组数目+1
        groupMapper.modifyCurNum(groupMonitor.getG_user_numcur()+1,groupMonitor.getG_id());
        //注意：分组数目+1，groupMonitor对象中也需要+1
        groupMonitor.setG_user_numcur(groupMonitor.getG_user_numcur()+1);
        //完善userMonitor对象
        userMonitor.setGroupMonitor(groupMonitor);
        //向用户监控表中插入一条信息
        userMapper.insertUserMonitor(userMonitor);
        //添加定时器
        //job任务
        JobDetail jobDetail = JobBuilder
                .newJob(UserJob.class)
                .withIdentity(userMonitor.getU_name(),groupMonitor.getG_name())
                .usingJobData("u_id",userMonitor.getU_id())
                .usingJobData("name",userMonitor.getU_name())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(userMonitor.getU_name(),groupMonitor.getG_name())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds((int) groupMonitor.getG_time()).repeatForever())
                .build();
        try {
            scheduler.scheduleJob(jobDetail,trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return userMonitor;
    }

    //删除定时器
    @Override
    public SinaUserMonitor deleteUserMonitor(SinaUserMonitor userMonitor) {
        //删除用户
        userMapper.deleteUserMonitor(userMonitor.getU_id());
        //获取分组信息
        SinaGroupMonitor groupMonitor = groupMapper.queryGroupByG_name(userMonitor.getGroupMonitor().getG_name());
        //修改分组信息
        groupMapper.modifyCurNum(groupMonitor.getG_user_numcur()-1,groupMonitor.getG_id());
        //删除定时器
        try {
            boolean flag = scheduler.checkExists(new JobKey(userMonitor.getU_name(),groupMonitor.getG_name()));
            if(flag){
                scheduler.deleteJob(new JobKey(userMonitor.getU_name(),groupMonitor.getG_name()));
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        //更新userMonitor对象   没有绑定分组
        userMonitor.setGroupMonitor(null);
        return userMonitor;
    }

    //修改用户监控信息
    //不完美  没有处理 同名修改 这个情况在controller除去
    @Override
    public SinaUserMonitor modifyGroupUserMonitor(SinaUserMonitor userMonitor, int g_id) {
        //更新用户分组外键
        userMapper.updateUserMonitorGroup(userMonitor.getU_id(),g_id);
        System.out.println("before = "+userMonitor.getGroupMonitor());
        //查询新的分组参数
        SinaGroupMonitor groupMonitor = groupMapper.queryGroupById(g_id);
        //修改group表
        //新组cur+1
        groupMapper.modifyCurNum(groupMonitor.getG_user_numcur()+1,g_id);
        //修改定时器 原分组数目-1
        //判断是否有当前定时器，如果有就删除再新建，如果没有直接新建
        if(userMonitor.getGroupMonitor() != null){
            try {
                //原分组数目-1
                groupMapper.modifyCurNum(userMonitor.getGroupMonitor().getG_user_numcur()-1,userMonitor.getGroupMonitor().getG_id());
                //更新userMonitor对象
                //注意：groupMonitor对象也需要进行修改
                groupMonitor.setG_user_numcur(groupMonitor.getG_user_numcur()+1);
                userMonitor.setGroupMonitor(groupMonitor);
                //如果不存在，返回false，不报错，不需要再进行额外判断。
                scheduler.deleteJob(new JobKey(userMonitor.getU_name(), userMonitor.getGroupMonitor().getG_name()));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        System.out.println("after  = "+userMonitor.getGroupMonitor());
        //添加定时器
        //job任务
        JobDetail jobDetail = JobBuilder
                .newJob(UserJob.class)
                .withIdentity(userMonitor.getU_name(),groupMonitor.getG_name())
                .usingJobData("u_id",userMonitor.getU_id())
                .usingJobData("name",userMonitor.getU_name())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(userMonitor.getU_name(),groupMonitor.getG_name())
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds((int) groupMonitor.getG_time()).repeatForever())
                .build();
        try {
            scheduler.scheduleJob(jobDetail,trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return userMonitor;
    }


    @Override
    public void modifyUserMonitorBlogNum(long uid, long blognum, long hot) {
        userMapper.updateUserMonitorBlogNum(uid,blognum,hot);
    }

    @Override
    public List<SinaUserMonitor> getAll() {
        return userMapper.queryAll();
    }

}

