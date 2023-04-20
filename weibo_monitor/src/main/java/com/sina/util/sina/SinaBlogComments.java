package com.sina.util.sina;

import com.sina.pojo.SinaComments;
import com.sina.pojo.User;
import com.sina.util.ConvertUtil;
import com.sina.util.EmotionUtil;
import com.sina.util.MySqlConnectionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaBlogComments {
    public static List<SinaComments> getComments(String mid) {
        List<SinaComments> list = null;
        String target = "https://weibo.com/aj/v6/comment/small?mid="+mid;
        try {
            //初始化
            HttpURLConnection urlConn = getHttpURLConnection(target,false);
            int count = 1;
            while (urlConn.getResponseCode() != 200&count<10) {
                urlConn = getHttpURLConnection(target,true);
                count++;
            }
            if(urlConn.getResponseCode() == 200){
                //获取网页
                String word = getRawHtml(urlConn);
                //获取数据      数据存储在user.comments
                list = foramt_data(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<SinaComments> foramt_data(String word) {
        List<SinaComments> list = new ArrayList<>();
        String tt = "\"html\":\"(.*),\"count\":";
        Pattern pattern = Pattern.compile(tt);
        Matcher matcher = pattern.matcher(word);
        String rawCommit = "";
        if (matcher.find()) {
            String group = matcher.group(1);
            rawCommit = ConvertUtil.unicodeToString(group);
        }
        Document total = Jsoup.parse(rawCommit);
        Elements comments = total.getElementsByAttributeValue("node-type", "root_comment");
        for (Element singleComments : comments) {
            Element imgDoc = singleComments.getElementsByClass("WB_face W_fl").get(0);
            Element img = imgDoc.getElementsByTag("img").get(0);
            String username = img.attr("alt");
            String imgUrl = img.attr("src");
            String uid = img.attr("usercard").substring(3);
            String host = "https:" + imgDoc.child(0).attr("href");

            //内容
            Element e_content = singleComments.getElementsByClass("WB_text").get(0);
            String content = e_content.text().split("^.*?：")[1];

            //点赞数
            Element e_like_before = singleComments.getElementsByClass("W_ficon ficon_praised S_txt2").get(0);
            Element e_like = e_like_before.nextElementSibling();
            String like = e_like.text();
            //对0赞的处理
            if(like.contains("赞")){
                like = "0";
            }

            //时间
            Element e_time = singleComments.getElementsByClass("WB_from S_txt2").get(0);
            String time = e_time.text();
//
            SinaComments sinaComments = new SinaComments();
            sinaComments.setPub_time(time);
            sinaComments.setContext(content);
            sinaComments.setLike(Long.parseLong(like));
            User user = new User(Long.parseLong(uid), username, imgUrl, host, null, null, null, null);
            sinaComments.setUser(user);
            //情感   我们不再合并判断
            //sinaComments.setEmotion(EmotionUtil.getWordEmotion(content));
            list.add(sinaComments);
        }
        return list;
    }

    private static String getRawHtml(HttpURLConnection urlConn) throws IOException {
        //获取输入流并读取内容
        InputStream inputStream = urlConn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String line = "";
        String word = "";
        while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
            line = line.replace("\\r", "");
            line = line.replace("\\n", "");
            line = line.replace("\\t", "");
            line = line.replace("\\\"", "\"");
            line = line.replace("\\/", "/");
            line = line.replace(">n", ">");
            word += line;
        }
        br.close();
        return word;
    }

    //在这里添加代理
    private static HttpURLConnection getHttpURLConnection(String target,boolean useProxy) throws IOException {
        HttpURLConnection urlConn = null;
        if(useProxy){
            String hostport = MySqlConnectionUtil.getHost();
            String host = hostport.split(":")[0];
            int port = Integer.parseInt(hostport.split(":")[1]);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,port));
            URL url = new URL(target);
            urlConn = (HttpURLConnection) url.openConnection(proxy);
        }else{
            URL url = new URL(target);
            urlConn = (HttpURLConnection) url.openConnection();
        }
        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConn.setRequestProperty("Charset", "utf-8");
        urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        urlConn.setRequestProperty("Accept", "*/*");
        urlConn.setRequestProperty("Accept-Language", "zh-cn");
        urlConn.setRequestProperty("cookie", "SCF=Au2eJQkyRy3oY5E_toowetXk7pnv7v9F1NnzAlpOcwPzAUrw8BqNpHS_aRlQnPJJWME6mjd_93QFlgRdxZW6q9A.; UOR=souky.eol.cn,widget.weibo.com,cn.bing.com; SINAGLOBAL=8209711189153.645.1581563099315; ULV=1616203893344:92:80:41:2501337776855.4634.1616203893341:1616146484708; webim_unReadCount=%7B%22time%22%3A1616147634920%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5ME1Qa7AH5CbcsV.zi1T_N; ALF=1647682480; SUB=_2AkMXCBSof8NxqwFRmPgcyWLiZYR1zQvEieKhVOVzJRMxHRl-yT9kqk87tRB6PIg6R2wzUr862dWmDkZM5YEb5M_vD5kM; login_sid_t=3ee402677939fc8f0559de10e2ffe109; cross_origin_proto=SSL; _s_tentry=cn.bing.com; Apache=2501337776855.4634.1616203893341; wb_view_log=1536*8641.25; WBtopGlobal_register_version=2021032010");
        return urlConn;
    }
}
