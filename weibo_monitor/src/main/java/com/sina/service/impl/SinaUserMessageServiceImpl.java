package com.sina.service.impl;

import com.sina.pojo.User;
import com.sina.service.SinaUserMessageService;
import com.sina.util.sina.SinaQueryUserDetail;
import com.sina.util.sina.SinaQueryUserList;
import org.springframework.stereotype.Service;

import java.util.List;
/*
* 如何获取Cookie?
请求俩接口得到必要的参数就ok了
接口1：https://passport.weibo.com/visitor/genvisitor?cb=gen_callback（返回tid）
接口2：https://passport.weibo.com/visitor/visitor?a=incarnate&t=接口1里的tid&w=3&c=100&cb=cross_domain&from=weibo（返回sub和subp）
Cookie：Sub=接口2的sub; SUBP=接口2的subp
*
* */
@Service
public class SinaUserMessageServiceImpl implements SinaUserMessageService {

    //从互联网上查询用户
    @Override
    public List<User> searchUserFromInet(String username) {
        //初始化
        List<User> userList = SinaQueryUserList.queryUserList(username);
        return userList;
    }

    @Override
    public List<User> queryFromES(long uid) {
        return null;
    }

    //2.对上述信息进行补充
//mid补充  微博数 关注和粉丝数目补充
    @Override
    public User spiderUserDetail(User u) {
        User user = SinaQueryUserDetail.queryUserDetail(u);
        return user;
    }
}
/*
<!-- 用户card-->
<div class="card card-user-b s-pg16 s-brt1">
  头像
  <div class="avator">
	<a>
	  <img src="">
	</a>
  </div>

  基本信息
  <div class="info">
	<div>
		个人主页
	  <a href="">用户名</a>
	  ...
	  <a href="" class="s-btn-c" uid="">
	</div>

	<!--第1个p-->
	<p>
		地址
	  <i class="icon-sex icon-sex-male">山东青岛</i>
	</p>

	<!--第2个p  可不存在-->
	<p>军事博主 军事视频自媒体</p>

	<!--第3个p-->
	<p>
	  <span>
		关注	提取uid
		<a href="//weibo.com/5062256772/follow">102</a>
	  </span>
	  <span>粉丝
		<a>5万</a>
	  </span>
	  <span class="s-nobr">微博
		<a>572</a>
	  </span>
	</p>

	<!--第4个p-->
	<p>简介：放过你自己 ✋🏼</p>

	<!--之后省略-->
	<p>	未提取 删除
		标签：
	</p>
 </div>
*/