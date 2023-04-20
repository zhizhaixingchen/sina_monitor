package com.sina.service.impl;

import com.sina.dao.SinaTopicDetailDao;
import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaTopicBean;
import com.sina.quartzJob.TopicJob;
import com.sina.service.SinaTopicdetailService;
import com.sina.util.sina.SinaCommonTopic;
import org.quartz.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
/*
总情感因子计算法则
    1.话题情感因子和热门微博第一个影响力相同
    2.影响因子计算    点赞*1+评论*2+转发*4
    3.计算总式  (话题情感分*微博第一影响因子+相应微博情感分*影响因子)/影响因子总和
    4.
 */
@Service
public class SinaTopicServiceImpl implements SinaTopicdetailService {


    private SinaTopicDetailDao sinaTopicDetailDao;
    private final Scheduler scheduler;
    public SinaTopicServiceImpl(SinaTopicDetailDao sinaTopicDetailDao, Scheduler scheduler) {
        this.sinaTopicDetailDao = sinaTopicDetailDao;
        this.scheduler = scheduler;
    }

    @Override
    public SinaTopicBean getSinaTopicByKeyWordFromInet(String keyword,SinaTopBean sinaTopBean) {

        SinaTopicBean sinaTopicBean = SinaCommonTopic.getCommonTopic(keyword, sinaTopBean);

        return sinaTopicBean;
    }

    //从es数据库中获取数据
    @Override
    public List<SinaTopicBean> getSinaTopicByKeywordFromDB(String keyword) {
        //去除空格
        keyword = keyword.trim();
        //分页获取
        List<SinaTopicBean> total_fromDB = new ArrayList<>();
        int page = 0;
        List<SinaTopicBean> hotFromDB = sinaTopicDetailDao.getSinaTopicBeanFromDB(keyword, 10000*page++);
        total_fromDB.addAll(hotFromDB);
        //对数据进行分页获取
        while(hotFromDB.size()==10000){
            hotFromDB = sinaTopicDetailDao.getSinaTopicBeanFromDB(keyword, 10000*page++);
            total_fromDB.addAll(hotFromDB);
        }
        return total_fromDB;
    }

    @Override
    public void test(){

    }

    //向定时器中追加topic监控
    @Override
    public boolean addMonitor(String topicName) {
        boolean flag = true;

        try {
            boolean exist = scheduler.checkExists(new JobKey(topicName,"topic"));
            //先判断是否存在，如果不存在新建，如果存在则重新执行
            if(!exist){
                JobDetail jobDetail = JobBuilder
                        .newJob(TopicJob.class)
                        .withIdentity(topicName,"topic")
                        .usingJobData("topicName",topicName)
                        .build();
                //由于一个trigger只能使用在一个topic上,因此一直使用默认trigger这种思想有问题
                //每一个job都需要指定的trigger
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(topicName,"topic")
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(60).repeatForever())
                        .build();
                scheduler.scheduleJob(jobDetail,trigger);
            }else{
                scheduler.resumeJob(new JobKey(topicName,"topic"));
            }
        } catch (SchedulerException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean removeMonitor(String topicName) {
        boolean flag = true;
        try {
            flag = scheduler.checkExists(new JobKey(topicName,"topic"));
            if(flag){
                flag = scheduler.deleteJob(new JobKey(topicName, "topic"));
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return flag;
    }

}

/*
基本话题信息
<div class="card-topic-a" node-type="topicSmall">
	<img/>照片
	<div class="info">
		<div class="title">
			<h1>
				<a href="">#黑龙江15岁女生弑母藏尸冷库#</a>  标题名称
			</h1>
		</div>

            		<div class="total">
              			<span>阅读3.2亿</span>			阅读
              			<span>讨论1.4万</span>			讨论
            		</div>
	</div>
</div>

*/


/*
<div class="m-con-l" id="pl_feedlist_index">
    <div>
        循环部分
                            mid
        <div class="card-wrap" action-type="feed_list_item" mid="4615703519299413">
            <div class = "card">
                <div class="card-feed">
                    <div class="avator">
                        <a><img/></a>	头像
                    </div>
                    <div class="content" node-type="like">
                                                昵称
                        <p class="txt" node-type="feed_list_content" nick-name="星星体育大爆炸">内容</p>
                        <p class="txt" node-type="feed_list_content_full" nick-name="伍倩MargotWU" style="display: none">内容 完整</p>
                        <p class="from"><a href="">发表时间</a></p>
                    </div>
                </div>
                <div class="card-act">
                    <ul>
                        均为<a></a>
                        <li></li>  收藏
                        <li></li>	 转发
                        <li></li>  评论
                        <li></li>  点赞
                </div>
            </div>
        </div>
</div>



*/