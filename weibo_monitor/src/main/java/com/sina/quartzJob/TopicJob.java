package com.sina.quartzJob;

import com.sina.dao.SinaTopicDetailDao;
import com.sina.pojo.SinaTopicBean;
import com.sina.util.sina.SinaCommonTopic;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Repository;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Repository
public class TopicJob extends QuartzJobBean {

    //获取话题名称
    private String topicName;

    private SinaTopicDetailDao sinaTopicDetailDao;
    public TopicJob(SinaTopicDetailDao sinaTopicDetailDao) {
        this.sinaTopicDetailDao = sinaTopicDetailDao;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //爬取指定数据
        SinaTopicBean commonTopic = SinaCommonTopic.getCommonTopic(topicName, null);
        //插入指定数据
        sinaTopicDetailDao.storeSinaTopic(commonTopic);

    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

}
