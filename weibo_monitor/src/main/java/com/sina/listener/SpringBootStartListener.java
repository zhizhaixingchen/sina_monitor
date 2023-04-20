package com.sina.listener;

import com.google.gson.JsonSyntaxException;
import com.sina.quartzJob.DayStatJob;
import com.sina.quartzJob.HotJob;
import com.sina.quartzJob.MonthStatJob;
import io.searchbox.client.JestClient;
import io.searchbox.core.Cat;
import io.searchbox.core.CatResult;
import io.searchbox.indices.CreateIndex;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class SpringBootStartListener implements ApplicationListener<ContextRefreshedEvent> {

    private Scheduler scheduler;
    private JestClient jestClient;
    public SpringBootStartListener(Scheduler scheduler, JestClient jestClient) {
        this.scheduler = scheduler;
        this.jestClient = jestClient;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //先进行表创建
        tableInit();
        //开启指定监控
        //1.热门话题监控
        monitorHotToES();
        //2.日统计监控
        monitorDayStat();
        //3.月统计监控
        monitorMonthStat();


    }

    //表创建
    //一共有三个表
    //mysql hive表已经手动创建
    //es一些表需要我们手动创建 我们在程序运行之前，需要判断es指定表是否初始化，如果没有初始化则进行初始化
    //keyword 不分词的index              sina oldsina sinatopic
    //使用ik分词器的index                sinatopicscan
    //默认就可以的                      sinahotstat oldsinahotstat users blogs

    //需要初始化的表 hot oldhot sinatopic sinatopicscan
    private void tableInit() {
        //hot
        boolean hot = indexIsExist("sina");
        if(!hot){
            initHotIndex("sina");
        }
        //oldhot
        boolean oldhot = indexIsExist("oldsina");
        if(!oldhot){
            initHotIndex("oldsina");
        }
        //sinatopic
        boolean sinatopic = indexIsExist("sinatopic");
        if(!sinatopic){
            initHotIndex("sinatopic");
        }

        //sinatopicscan
        boolean sinatopicscan = indexIsExist("sinatopicscan");
        if(!sinatopicscan){
            initIKIndex("sinatopicscan");
        }
    }

    //对热搜榜进行监控，默认情况1分钟，一天一共有1*60*24=1440分钟，即有1440*51 = 73440条数据
    private void monitorHotToES(){
        try {
            boolean b = scheduler.checkExists(new JobKey("hot", "hotGroup"));
            //如果b不存在，那么我们需要添加监控
            if(!b){
                JobDetail jobDetail = JobBuilder
                        .newJob(HotJob.class)
                        .withIdentity("hot","hotGroup")
                        .build();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("hot","hotGroup")
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //日监控 判断日监控是否打开
    private void monitorDayStat(){
        try {
            boolean exists = scheduler.checkExists(new JobKey("stat", "day"));
            if(!exists){
                System.out.println("day不存在");
                JobDetail jobDetail = JobBuilder
                        .newJob(DayStatJob.class)
                        .withIdentity("stat","day")
                        .build();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("stat","day")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 30 0 * * ?"))
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //月监控 判断月监控是否打开
    private void monitorMonthStat(){
        try {
            boolean exists = scheduler.checkExists(new JobKey("stat", "month"));
            if(!exists){
                System.out.println("month不存在");
                JobDetail jobDetail = JobBuilder
                        .newJob(MonthStatJob.class)
                        .withIdentity("stat","month")
                        .build();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("stat","month")
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 1 * ?"))
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    //keyword属性不分词，全匹配查询
    //对应的es有    sina oldsina sinatopic
    //部分需要重写
    private void initHotIndex(String index){
        String setting = "{\n" +
                "            analysis: {\n" +
                "                analyzer: {\n" +
                "                    default: {\n" +
                "                        type: keyword\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n";
        CreateIndex createIndex = new CreateIndex.Builder(index).settings(setting).build();
        try {
            jestClient.execute(createIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化包含ik分词器的index
    //建立topic_scan表
    private void initIKIndex(String index){
        String setting = "{\n" +
                "        \"index\" : {\n" +
                "            \"analysis.analyzer.default.type\": \"ik_max_word\"\n" +
                "        }\n" +
                "    }";
        CreateIndex createIndex = new CreateIndex.Builder(index).settings(setting).build();
        try {
            jestClient.execute(createIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //判断当前表是否存在
    private boolean indexIsExist(String index){
        boolean flag = false;
        Cat cat = new Cat.IndicesBuilder().addIndex(index).build();
        try {
            CatResult execute = jestClient.execute(cat);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JsonSyntaxException e){
            System.out.println(index+"已经存在");
        }
        return flag;
    }

}
