package com.sina.util.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job {
    private String data2;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //context为一个基本pojo类型+键值对
        //方式1   通过context.getJobDetail().getJobDataMap();获取
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String data = jobDataMap.getString("data");
        System.out.println("job执行 = "+data);

        //方式2   通过依赖注入方式获取  选择这一种
        System.out.println("method2 = "+data2);
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }
}
