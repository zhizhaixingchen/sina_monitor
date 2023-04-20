package com.sina.service.impl;

import com.sina.pojo.SinaComments;
import com.sina.service.SinaBlogCommentService;
import com.sina.util.sina.SinaBlogComments;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SinaBlogCommentServiceImpl implements SinaBlogCommentService {

    //获取最新评论列表  根据热度
    @Override
    public List<SinaComments> getSinaComments(long mid) {
        List<SinaComments> comments = SinaBlogComments.getComments(String.valueOf(mid));
        return comments;
    }
}
