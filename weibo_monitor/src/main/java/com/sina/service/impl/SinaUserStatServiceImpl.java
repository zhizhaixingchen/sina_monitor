package com.sina.service.impl;

import com.sina.dao.SinaUserStatDao;
import com.sina.dao.mapper.SinaUserMonitorMapper;
import com.sina.pojo.UserBlogES;
import com.sina.pojo.SingleBlog;
import com.sina.service.SinaBlogService;
import com.sina.service.SinaUserStatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SinaUserStatServiceImpl implements SinaUserStatService {
    private SinaUserStatDao userStatDao;
    private SinaBlogService blogService;
    private SinaUserMonitorMapper userMapper;
    public SinaUserStatServiceImpl(SinaUserStatDao userStatDao, SinaBlogService blogService, SinaUserMonitorMapper userMapper) {
        this.userStatDao = userStatDao;
        this.blogService = blogService;
        this.userMapper = userMapper;
    }

    @Override
    public List<SingleBlog> queryDefault(int from, int size) {
        List<SingleBlog> blogList = new ArrayList<>();
        List<UserBlogES> userBlogES = userStatDao.queryByField("current_date", "desc", from, size);
        //保证有数据
        if(userBlogES.size()>0){
            for (UserBlogES blog : userBlogES) {
                SingleBlog singleBlog = blogService.getSinaBlogFromInet(String.valueOf(blog.getUid()), String.valueOf(blog.getMid()));
                blogList.add(singleBlog);
            }
        }
        return blogList;
    }

    @Override
    public List<SingleBlog> queryBlogByGroup(int g_id, int from, int size) {
        List<SingleBlog> blogList = new ArrayList<>();
        //查询g_id对应uid
        List<Long> uidList = userMapper.queryUidByGid(g_id);
        //必须保证uidList有值才进入dao
        if(uidList.size()>0){
            //uid返回BlogES对象
            List<UserBlogES> userBlogES = userStatDao.queryByGroup(uidList, from, size);
            //从网络中获取信息
            if(userBlogES.size()>0){
                for (UserBlogES blog : userBlogES) {
                    SingleBlog singleBlog = blogService.getSinaBlogFromInet(String.valueOf(blog.getUid()), String.valueOf(blog.getMid()));
                    blogList.add(singleBlog);
                }
            }
        }
        return blogList;
    }

    @Override
    public List<SingleBlog> queryBlogByUser(String name, int from, int size) {
        List<SingleBlog> blogList = new ArrayList<>();
        List<UserBlogES> userBlogES = userStatDao.queryByname(name, from, size);
        //从网络中获取信息
        if(userBlogES.size()>0){
            for (UserBlogES blog : userBlogES) {
                SingleBlog singleBlog = blogService.getSinaBlogFromInet(String.valueOf(blog.getUid()), String.valueOf(blog.getMid()));
                blogList.add(singleBlog);
            }
        }
        return blogList;
    }

    @Override
    public List<SingleBlog> queryBlogByEmotion(String rotation, int from, int size) {
        List<SingleBlog> blogList = new ArrayList<>();
        List<UserBlogES> userBlogES = userStatDao.queryByField("emotion", rotation, from, size);
        //保证有数据
        if(userBlogES.size()>0){
            for (UserBlogES blog : userBlogES) {
                SingleBlog singleBlog = blogService.getSinaBlogFromInet(String.valueOf(blog.getUid()), String.valueOf(blog.getMid()));
                blogList.add(singleBlog);
            }
        }
        return blogList;
    }

    @Override
    public void insertData(List<UserBlogES> userBlogESList) {
        userStatDao.insertData(userBlogESList);
    }

    @Override
    public void insertData(UserBlogES userBlogES) {
        userStatDao.insertOne(userBlogES);
    }

}
