package com.sina.quartzJob;

import com.sina.pojo.UserBlogES;
import com.sina.pojo.SinaUserMonitor;
import com.sina.pojo.SingleBlog;
import com.sina.pojo.User;
import com.sina.service.SinaBlogService;
import com.sina.service.SinaUserMonitorService;
import com.sina.service.SinaUserStatService;
import com.sina.util.sina.SinaQueryUserDetail;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Repository
public class UserJob extends QuartzJobBean {
    //uid是一个用户特有的标识，也可以作为一个定时器特有的标识
    private long u_id;
    //根据name查询当前用户
    private String name;
    private SinaUserStatService userStatService;
    private SinaUserMonitorService userMonitorService;
    private SinaBlogService sinaBlogService;
    public UserJob(SinaUserStatService userStatService, SinaUserMonitorService userMonitorService, SinaBlogService sinaBlogService) {
        this.userStatService = userStatService;
        this.userMonitorService = userMonitorService;
        this.sinaBlogService = sinaBlogService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //根据uid查询当前用户
        SinaUserMonitor userMonitor = userMonitorService.getUserMonitor(u_id);
        long dbBlogNum = userMonitor.getU_blognum();
        System.out.println("uname = "+name);
        //使用爬虫爬取当前微博数目
        //基本信息已经存储到数据库中
        User userBefore = new User(userMonitor.getU_id(),userMonitor.getU_name(),userMonitor.getU_imgUrl(),userMonitor.getU_host(),userMonitor.getU_location(),"","","");
        User user = SinaQueryUserDetail.queryUserDetail(userBefore);

        long nowblogNum = Long.parseLong(user.getBlogNum());
        int num = (int) (nowblogNum-dbBlogNum);
        System.out.println(name+"oldNum = "+dbBlogNum+"\tnewNum = "+nowblogNum);
        if(num>0){
            //获取mid
            List<String> currentBlog = user.getCurrentBlog();
            //存储微博
            List<SingleBlog> singleBlogList = new ArrayList<>();
            int i = 0;
            //如果有一条 0 1执行  11不执行
            while(singleBlogList.size()<num){
                SingleBlog singleBlog = sinaBlogService.getSinaBlogFromInet(String.valueOf(u_id), currentBlog.get(i++));
                System.out.println("singleBlog = "+singleBlog);
                if(singleBlog!=null){
                    singleBlogList.add(singleBlog);
                }
            }
            //只有一条
            if(singleBlogList.size() == 1){
                SingleBlog blog = singleBlogList.get(0);
                UserBlogES userBlogES = new UserBlogES(blog.getCurrent_date(),blog.getName(),Long.parseLong(blog.getUid()),blog.getMid(),blog.getEmotion());
                //向ES中插入数据
                userStatService.insertData(userBlogES);
            }else{
                //进行倒置，最新的在最后面
                Collections.reverse(singleBlogList);
                List<UserBlogES> esList = new ArrayList<>();
                for (SingleBlog blog : singleBlogList) {
                    UserBlogES userBlogES = new UserBlogES(blog.getCurrent_date(),blog.getName(),Long.parseLong(blog.getUid()),blog.getMid(),blog.getEmotion());
                    esList.add(userBlogES);
                }
                //向ES中插入数据
                userStatService.insertData(esList);
            }
            //更新mysql
            userMonitorService.modifyUserMonitorBlogNum(u_id,nowblogNum,Long.parseLong(user.getFans()));
        }
        //说明用户存在删除微博的情况，我们进行数据更新
        if(num<0){
            userMonitorService.modifyUserMonitorBlogNum(u_id,nowblogNum,Long.parseLong(user.getFans()));
        }

    }

    public long getU_id() {
        return u_id;
    }
    public void setU_id(long u_id) {
        this.u_id = u_id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
