package com.sina.quartzJob;

import com.sina.dao.SinaHotTopicStatDao;
import com.sina.dao.SinaTopDao;
import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaHotTopicStatBean;
import com.sina.util.sina.SinaHotTop;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Repository
public class HotJob extends QuartzJobBean {

    private SinaTopDao sinaTopDao;
    private SinaHotTopicStatDao hotTopicStatDao;
    public HotJob(SinaTopDao sinaTopDao,SinaHotTopicStatDao hotTopicStatDao) {
        this.sinaTopDao = sinaTopDao;
        this.hotTopicStatDao = hotTopicStatDao;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //爬取指定数据
        List<SinaTopBean> sinaHotTop = SinaHotTop.getSinaHotTop();
        //插入指定数据至ES
        sinaTopDao.storeCurrentHotData(sinaHotTop);

        //向es sinahotstat中存入数据,用于今日统计
        SinaHotTopicStatBean bean = getBean(sinaHotTop);
        hotTopicStatDao.insertData(bean);

    }


    private SinaHotTopicStatBean getBean(List<SinaTopBean> list){
        //计算规则      第一条为新浪微博推荐，后台不显示热度 我们设置其热度等于热榜最后一条的热度
        //热度计算
        long t_hot = 0;
        //情感值计算
        double t_influence = 0;           //被除数
        //对i == 0 进行处理
        t_hot += list.get(list.size()-1).getAccess_count();
        t_influence += list.get(0).getEmotion_point()*list.get(list.size()-1).getAccess_count();
        for (int i = 1;i<list.size();i++) {
            SinaTopBean sinaTopBean = list.get(i);
            t_influence += sinaTopBean.getEmotion_point()*sinaTopBean.getAccess_count();
            t_hot += sinaTopBean.getAccess_count();
        }
        double t_emotion = t_influence/t_hot;
        SinaHotTopicStatBean stms = new SinaHotTopicStatBean(System.currentTimeMillis(),t_hot,t_emotion);
        return stms;
    }
}
