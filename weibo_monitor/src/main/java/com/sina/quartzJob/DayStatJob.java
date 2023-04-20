package com.sina.quartzJob;

import com.sina.service.SinaDMStatService;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Repository;

//今日工作
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Repository
public class DayStatJob extends QuartzJobBean {

    private SinaDMStatService dmStatService;
    public DayStatJob(SinaDMStatService dmStatService) {
        this.dmStatService = dmStatService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        //今日数据清理
        dmStatService.dayWork();
        System.out.println("day执行了");
    }
}
