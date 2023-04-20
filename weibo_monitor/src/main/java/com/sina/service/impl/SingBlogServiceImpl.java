package com.sina.service.impl;

import com.sina.pojo.SingleBlog;
import com.sina.service.SinaBlogService;
import com.sina.util.ConvertUtil;
import com.sina.util.sina.SinaSingleBlog;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SingBlogServiceImpl implements SinaBlogService {

    @Override
    public SingleBlog getSinaBlogFromInet(String uid,String mid) {
        SingleBlog blog = SinaSingleBlog.getBlog(uid,mid);
        return blog;
    }


    @Override
    public List<SingleBlog> getSingleBlogFromDB(String mid) {
        return null;
    }

    @Override
    public List<SingleBlog> getSinaBlogListFromInet(long uid, List<String> currentBlog) {
        List<SingleBlog> list = new ArrayList<>();
        for (String mid : currentBlog) {
            SingleBlog blog = SinaSingleBlog.getBlog(String.valueOf(uid), mid);
            //针对出现爬取失败的情况
            if(blog!=null){
                list.add(blog);
            }
        }
        return list;
    }
}
