package com.sina.util.quartz.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//定时任务线程池  接TimerTest1
public class TimerTest2 {

    public static void main(String[] args) {
        //定义定时任务线程池
        ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(5);
        //Executors.newSingleThreadExecutor(1);     定义单线程
        for (int i=0;i<2;i++){
            MyTimeTask myTimeTask = new MyTimeTask();
            myTimeTask.setName("name-->"+i);
            //schedule只执行一次
            //scheduledPool.schedule(myTimeTask,2, TimeUnit.SECONDS);
            //scheduleAtFixedTate   拥有执行周期
            scheduledPool.scheduleAtFixedRate(myTimeTask,0,2,TimeUnit.SECONDS);
        }
    }

}
