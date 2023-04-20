package com.sina.quartzJob;

import com.sina.pojo.MonitorBlogES;
import com.sina.pojo.SinaComments;
import com.sina.pojo.SingleBlog;
import com.sina.service.SinaBlogCommentService;
import com.sina.service.SinaBlogMonitorService;
import com.sina.service.SinaBlogService;
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
public class BlogJob extends QuartzJobBean {
    private long mid;
    private long uid;
    private SinaBlogMonitorService sinaBlogMonitorService;
    private SinaBlogService blogService;
    private SinaBlogCommentService commentService;
    public BlogJob(SinaBlogMonitorService sinaBlogMonitorService, SinaBlogService blogService, SinaBlogCommentService commentService) {
        this.sinaBlogMonitorService = sinaBlogMonitorService;
        this.blogService = blogService;
        this.commentService = commentService;
    }
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //根据uid mid获取该微博最新数据
        SingleBlog singleBlog = blogService.getSinaBlogFromInet(String.valueOf(uid), String.valueOf(mid));
        //获取评论数据
        List<SinaComments> sinaCommentList = commentService.getSinaComments(mid);
        //进行情感分析    获取最终情感值
        double finalEmotion = emotionAnalysis(sinaCommentList,singleBlog.getEmotion());
        //组装存储对象
        MonitorBlogES mbe = new MonitorBlogES(singleBlog.getCurrent_date(),Long.parseLong(singleBlog.getUid()),singleBlog.getMid(),singleBlog.getLike(),singleBlog.getTransfer(),singleBlog.getComments_num(),finalEmotion);
        //插入数据
        sinaBlogMonitorService.insertMonitorBlogData(mbe);
    }

    private double emotionAnalysis(List<SinaComments> sinaCommentList,double blogEmotion) {
        //计算评论情感值
        long hot = 0;
        double middle = 0;      //中间值
        for (SinaComments comments : sinaCommentList) {
            double tmpEmo = comments.getEmotion();
            double tmpHot = comments.getLike();
            middle += tmpEmo*tmpHot;
            hot += tmpHot;
        }
        double commentsEmotion = middle/hot;
        //总情感值
        double finalEmotion = blogEmotion*0.6+commentsEmotion*0.4;
        return finalEmotion;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

}
