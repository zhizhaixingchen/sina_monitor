package com.sina.quartzJob;

import com.sina.service.SinaDMStatService;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Repository;

//每月工作
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Repository
public class MonthStatJob extends QuartzJobBean {

    private SinaDMStatService dmStatService;
    public MonthStatJob(SinaDMStatService dmStatService) {
        this.dmStatService = dmStatService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //每月数据数据清理
        dmStatService.monthWork();
        System.out.println("月监控执行");

    }
}
