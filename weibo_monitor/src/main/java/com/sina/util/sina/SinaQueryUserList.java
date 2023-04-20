package com.sina.util.sina;

import com.sina.pojo.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SinaQueryUserList {
    public static List<User> queryUserList(String username){
        HttpURLConnection urlConn = queryUserListInit(username);
        String rawData = userListRawData(urlConn);
        List<User> userList = userListFormatData(rawData);
        return userList;
    }
    //1.查询用户相关爬虫
    //初始化爬虫工具
    private static HttpURLConnection queryUserListInit(String username){
        try {
            username = URLEncoder.encode(username,"utf-8");
            String target = "https://s.weibo.com/user?q="+username+"&Refer=SUer_box";
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
            return urlConn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //获取网页原始数据
    private static String userListRawData(HttpURLConnection urlConn) {
        String word = "";
        try {
            int code = urlConn.getResponseCode();
            if(code == 200){
                //获取输入流并读取内容
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    word+=line;
                }
                br.close();
                return word;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return word;
    }
    //格式化数据
    private static List<User> userListFormatData(String rawData) {
        List<User> list = new ArrayList<>();
        Document doc = Jsoup.parse(rawData);
        Element body = doc.body();
        Elements rawUserList = body.getElementsByClass("card card-user-b s-pg16 s-brt1");
        for (Element element : rawUserList) {
            //头像
            Element avator = element.getElementsByClass("avator").get(0);
            Element img = avator.getElementsByTag("img").get(0);
            String imgUrl = img.attr("src");

            //获取基本信息
            Element info = element.getElementsByClass("info").get(0);
            Element href = info.getElementsByClass("name").get(0);
            //获取昵称
            String username = href.wholeText();
            //获取个人主页
            String host = "https:"+href.attr("href");
            //获取uid
            String uid_str = info.getElementsByClass("s-btn-c").get(0).attr("uid");
            long uid = Long.parseLong(uid_str);

            Elements pList = info.getElementsByTag("p");
            //获取地址
            Element p_loc = pList.get(0);
            String location = p_loc.text().trim().replace(" 个人主页","");

            //寻找关注、粉丝、微博行
            Elements three = info.getElementsByTag("span");
            //关注
            Element e_focus = three.get(0).child(0);
            String focus = e_focus.text();

            //粉丝
            Element e_fans = three.get(1).child(0);
            String fans = e_fans.text();

            //blog
            Element e_blogs = three.get(2).child(0);
            String blogNum = e_blogs.text();

            //获取简介
            Element p = three.parents().get(0);
            Element p_des = p.nextElementSibling();
            String description = "";
            if(p_des != null&&p_des.text().startsWith("简介")){
                description = p_des.text().replace("简介：","");
            }
            User user = new User(uid,username,imgUrl,host,location,blogNum,focus,fans);
            //user.setCurrent_date(System.currentTimeMillis());
            user.setDescription(description);
            list.add(user);
        }

        return list;
    }

}
