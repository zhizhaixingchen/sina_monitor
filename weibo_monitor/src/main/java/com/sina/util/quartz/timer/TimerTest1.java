package com.sina.util.quartz.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//基本定时任务
public class TimerTest1 {
    public static void main(String[] args) {
        Timer timer = new Timer();  //定时器启动
        for (int i=0;i<2;i++) {
            MyTimeTask myTimeTask = new MyTimeTask();
            myTimeTask.setName("task"+i);
            timer.schedule(myTimeTask,new Date(1616724754));
            //timer.scheduleAtFixedRate(myTimeTask,new Date(),2000);

/*
            timer.schedule(myTimeTask,new Date(1616724754));
            timer.scheduleAtFixedRate();
            在执行任务时间小于间隔时间时，这两个方法一致
            当执行任务时间大于间隔时间时，这两个方法的区别：
            schedule 超时会立即执行
            scheduleAtFixedRate 超时会等到指定时间点执行
            example     定时任务周期  2s      定时任务执行时间    3s
                正常任务执行时间           12:00:00   12:00:02    12:00:04    12:00:04    ...
                schedule                12:00:00   12:00:03    12:00:06    12:00:09   (定时器失效)
                scheduleAtFixedRate     12:00:00   12:00:04    12:00:08    12:00:12   (按照之前既定的时间准则执行)
            */

            /*
                1.使用普通Timer的劣势：
                指定的定时任务在执行时为单线程，即一个定时任务一个定时任务执行，不利于管理
                2.改进
                我们需要在run方法中启用线程池


            */
        }




    }
}

class MyTimeTask extends TimerTask{
    private String name;
    @Override
    public void run() {
        try {
            System.out.println(name+"\t start = "+new Date());
            Thread.sleep(3000);
            System.out.println(name+"\t end = "+new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
