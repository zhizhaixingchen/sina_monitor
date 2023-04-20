package com.sina.service;

import com.sina.pojo.SinaComments;

import java.util.List;

//获取微博评论数据
public interface SinaBlogCommentService {

    List<SinaComments> getSinaComments(long mid);
}
