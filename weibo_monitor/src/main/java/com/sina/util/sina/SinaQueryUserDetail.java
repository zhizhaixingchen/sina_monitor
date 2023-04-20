package com.sina.util.sina;

import com.sina.pojo.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaQueryUserDetail {
    public static User queryUserDetail(User u){
        HttpURLConnection urlConn = detailUserinit(u.getHost());
        String s = deatilRawData(urlConn);
        User user = detailFormatData(s,u);
        return user;
    }
    //初始化
    private static HttpURLConnection detailUserinit(String host){
        URL url = null;
        try {
            //追加?profile_ftype=1&is_ori=1#_0之后主页按照时间进行排序
            url = new URL(host+"?profile_ftype=1&is_ori=1#_0");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
            urlConn.setRequestProperty("Cookie","SINAGLOBAL=3208745395199.8545.1614866074873; SCF=AidVvHEK1PoCzwNnMIB9sFD8Ywktc8YjRlrbeURe1nlIKr5SjRj5bG_10Kd9RPk9tBeYFd85vamlFcrjII6LS3o.; SUB=_2AkMXE4jIdcPxrAVUmv8WyWLib4VH-jykxuE-An7uJhMyAxh77lIPqSVutBF-XACGB3cks1qTRUHFfqud3gfVBeRb; SUBP=0033WrSXqPxfM72wWs9jqgMF55529P9D9Whi0W3JuwfF1Unn_oLXIlNf5JpVF02feKe41hqcS0ep; UOR=cn.bing.com,www.weibo.com,cn.bing.com; _s_tentry=-; Apache=1318228900113.0166.1616113143159; ULV=1616113143191:15:15:13:1318228900113.0166.1616113143159:1616068962477; WBtopGlobal_register_version=2021031909; login_sid_t=621f0d7b80ab787c3659532c01a293ab; cross_origin_proto=SSL; wb_view_log=1536*8641.25");
            return urlConn;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String deatilRawData(HttpURLConnection urlConn){
        String word = "";
        try {
            if(urlConn!=null&&urlConn.getResponseCode() == 200){
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = "";
                word = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    line = line.replace("\\r", "");
                    line = line.replace("\\n", "");
                    line = line.replace("\\t", "");
                    line = line.replace("\\", "");
                    word+=line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return word;
    }

    private static User detailFormatData(String rawData, User user) {
        List<String> list = new ArrayList<>();
        //获取mid值
        String mid_partten = "\\smid=\"([0-9]{16})\"";
        Pattern p = Pattern.compile(mid_partten);
        Matcher mid_matcher = p.matcher(rawData);
        while(mid_matcher.find()){
            String group = mid_matcher.group(1);
            list.add(group);
        }
        user.setCurrentBlog(list);

        //获取三样focus fans blog           注意：这里的表达式可能存在问题W_f1[0-9]  第一个数字未发现非1的情况，但是并不代表没有
                                        //注意:   表达式问题2 央视新闻不包含(W_f1)
        //String p_str = "<strong \"^W_f1[0-9]$?\">([0-9]+)</strong>";
        String p_str = "<strong class=\"(W_f1[0-9])?\">([0-9]+)</strong>";
        Pattern p_t = Pattern.compile(p_str);
        Matcher t_matcher = p_t.matcher(rawData);
        if(t_matcher.find()){
            String focus = t_matcher.group(2);
            user.setFocus(focus);
        }
        //获取粉丝
        if(t_matcher.find()){
            String fans = t_matcher.group(2);
            user.setFans(fans);
        }
        //获取微博
        if(t_matcher.find()){
            String vblog = t_matcher.group(2);
            user.setBlogNum(vblog);
        }
        return user;
    }
}
