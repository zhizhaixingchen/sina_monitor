package com.sina.util.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;
//Quartz优势
//Scheduler每次执行，都会根据jobDetail创建一个新的job实例，这样就可以实现同一个job并发执行，(period 2s 执行时间大于2s的问题)
//在指定的Job类前设置@DisallowConcurrentExecution可以转换为非并发   即:每次执行都是一个实例
//对于并发执行存在的一个问题：每次并发执行都会产生一个新的JobDetail对象，导致存储在原来JobDeatil中的Data数据消失
//对于这个问题，我们可以使用@PersistJobDataAfterExecution注解 来持久化JobDetail中的JobDataMap（对trigger中的jobdataMap无效）

//我们对于这两个注解可选  在本项目中
public class QuartzMain {
    public static void main(String[] args) throws ParseException, SchedulerException {
        taskSchedule_1();
    }

    public static void taskSchedule_1() throws SchedulerException, ParseException {
        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = "2021-03-12 10:13:00";*/
        //创建schedule，schedule将Triggler(定时器)与Job绑定到一块，当Trigger触发，对应的Job就会执行
        //一个Job可以保存多个Trigger，但是一个Triggle只能拥有一个job
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();


        //job任务
        JobDetail jobDetail = JobBuilder
                .newJob(TestJob.class)
                .withIdentity("jobName1","jobGroup1")
                .usingJobData("data","当前时间-->"+new Date())
                .usingJobData("data2","hello-->"+new Date())
                .build();

        //Trigger定时器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("triggle-1","triggler")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever())
                .build();
        scheduler.scheduleJob(jobDetail,trigger);
    }
}

