package com.sina.util.sina;

import com.sina.pojo.SingleBlog;
import com.sina.util.ConvertUtil;
import com.sina.util.EmotionUtil;
import com.sina.util.MySqlConnectionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaSingleBlog {
    public static SingleBlog getBlog(String uid,String mid){
        SingleBlog singleBlog = null;
        String mid_url = getUrlFromMid(mid);
        String target = "https://weibo.com/"+uid+"/"+mid_url+"?wvr=6";
        try {
            //使用代理
            HttpURLConnection urlConn = getHttpURLConnection(target,false);
            int count = 1;
            while(urlConn.getResponseCode() != 200&count<10) {
                urlConn = getHttpURLConnection(target,true);
                count++;
            }
            if(urlConn.getResponseCode() == 200){
                String word = getRawHtml(urlConn);
                System.out.println(word);
                singleBlog = getResultByRe(uid,word);
                if(singleBlog!=null){
                    singleBlog.setMid(Long.parseLong(mid));
                    //设置原始网页地址
                    String rawLink = "https://weibo.com/"+uid+"/"+mid_url+"?wvr=6";
                    singleBlog.setMyUrl(rawLink);
                    //设置主键
                    singleBlog.setCurrent_date(System.currentTimeMillis());
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return singleBlog;
    }

    private static SingleBlog getResultByRe(String uid,String word) {
        //贪婪模式  * + {n,} 默认情况是贪婪模式匹配
        //非贪婪模式，?跟在 * + {n,} 等的后面时，表示非贪婪模式，注意和子表达式后面的?区分开，子表达式后的?表示匹配0次或1次
        String t = "(<div class=\"WB_text W_f14\" .*?</div>)";
        Pattern p = Pattern.compile(t);
        Matcher matcher = p.matcher(word);
        String rawDiv = "";
        if(matcher.find()){
            rawDiv = matcher.group(1);
        }
        Document parse = Jsoup.parse(rawDiv);
        //针对新浪微博的反爬虫机制
        try {
            //获取微博username
            String username = parse.body().child(0).attr("nick-name");
            //获取内容
            String context = parse.wholeText().replace(" ","");
            System.out.println(context.replace(" ",""));


            String t2 = "(<ul class=\"WB_row_line WB_row_r4 clearfix S_line2\">.*?</ul>)";
            Pattern p2 = Pattern.compile(t2);
            Matcher m2 = p2.matcher(word);
            String raw2 = "";
            if(m2.find()){
                raw2 = m2.group(1);
            }
            Document ul = Jsoup.parse(raw2);
            Element e_transfer_before = ul.getElementsByClass("W_ficon ficon_forward S_ficon").get(0);
            Element e_transfer = e_transfer_before.nextElementSibling();
            String transfer = e_transfer.text();
            //对于流量明星 评论转发100万+的情况
            transfer = handleEndChar(transfer);
            System.out.println("转发 = "+transfer);
            if(transfer.contains("转发")){
                //如果不存在转发或评论数目 则为0
                transfer = "0";
            }


            Element e_comment_before = ul.getElementsByClass("W_ficon ficon_repeat S_ficon").get(0);
            Element e_comment = e_comment_before.nextElementSibling();
            String comment = e_comment.text();
            //对于流量明星 评论转发100万+的情况
            comment = handleEndChar(comment);
            System.out.println("评论 = "+comment);
            if(comment.contains("评论")){
                //如果不存在转发或评论或点赞数目则为0
                comment = "0";
            }
            Element e_like_before = ul.getElementsByClass("W_ficon ficon_praised S_txt2").get(0);
            Element e_like = e_like_before.nextElementSibling();
            String like = e_like.text();
            System.out.println("点赞 = "+like);
            if(like.contains("赞")){
                //如果不存在转发或评论或点赞数目则为0
                like = "0";
            }
            //获取头像
            String t3 = "<img usercard=\".*?\" src=\"(.*?)\"";
            Pattern p3 = Pattern.compile(t3);
            Matcher m3 = p3.matcher(word);
            String imgUrl = "";
            if(m3.find()){
                imgUrl = m3.group(1);
            }

            //获取时间
            String t4 = "(<div class=\"WB_from S_txt2\">.*?</div>)";
            Pattern p4 = Pattern.compile(t4);
            Matcher m4 = p4.matcher(word);
            String rawpubTime = "";
            String pub_time = "";
            if(m4.find()){
                rawpubTime = m4.group(1);
                Document pub_div = Jsoup.parse(rawpubTime);
                Element child = pub_div.body().child(0).child(0);
                pub_time = child.attr("title");

            }
            SingleBlog singleBlog = new SingleBlog(0,username,uid,imgUrl,context,pub_time,Long.parseLong(transfer),Long.parseLong(comment),Long.parseLong(like));
            //判断情感
            //singleBlog.setEmotion(EmotionUtil.getWordEmotion(context));
            return singleBlog;
        }catch (IndexOutOfBoundsException e){
            System.out.println("未爬取到数据");
            return null;
        }
    }

    private static String getRawHtml(HttpURLConnection urlConn) throws IOException {
        //获取输入流并读取内容
        InputStream inputStream = urlConn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
        String line = "";
        String word = "";
        while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
            line = line.replace("\\r","");
            line = line.replace("\\n","");
            line = line.replace("\\t","");
            line = line.replace("\\","");
            line = line.replace(">n",">");
            word+=line;
        }
        br.close();
        return word;
    }

    //添加代理
    private static HttpURLConnection getHttpURLConnection(String target,boolean useProxy) throws IOException {
        HttpURLConnection urlConn = null;
        if(useProxy){
            String hostport = MySqlConnectionUtil.getHost();
            String host = hostport.split(":")[0];
            System.out.println("使用代理\t"+hostport);
            int port = Integer.parseInt(hostport.split(":")[1]);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,port));
            URL url = new URL(target);
            urlConn = (HttpURLConnection) url.openConnection(proxy);
        }else{
            URL url = new URL(target);
            urlConn = (HttpURLConnection) url.openConnection();
        }
        urlConn.setConnectTimeout(3000);
        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConn.setRequestProperty("Charset", "utf-8");
        urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        urlConn.setRequestProperty("Accept", "*/*");
        urlConn.setRequestProperty("Accept-Language", "zh-cn");
        urlConn.setRequestProperty("cookie","SCF=Au2eJQkyRy3oY5E_toowetXk7pnv7v9F1NnzAlpOcwPzAUrw8BqNpHS_aRlQnPJJWME6mjd_93QFlgRdxZW6q9A.; UOR=souky.eol.cn,widget.weibo.com,cn.bing.com; SINAGLOBAL=8209711189153.645.1581563099315; ULV=1616203893344:92:80:41:2501337776855.4634.1616203893341:1616146484708; webim_unReadCount=%7B%22time%22%3A1616147634920%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5ME1Qa7AH5CbcsV.zi1T_N; ALF=1647682480; SUB=_2AkMXCBSof8NxqwFRmPgcyWLiZYR1zQvEieKhVOVzJRMxHRl-yT9kqk87tRB6PIg6R2wzUr862dWmDkZM5YEb5M_vD5kM; login_sid_t=3ee402677939fc8f0559de10e2ffe109; cross_origin_proto=SSL; _s_tentry=cn.bing.com; Apache=2501337776855.4634.1616203893341; wb_view_log=1536*8641.25; WBtopGlobal_register_version=2021032010");
        return urlConn;
    }

    private static String getUrlFromMid(String uid) {
        //转换uid 注意 我们需要判断是否需要在最开始追加0
        String first = ConvertUtil.to_SixtyTwo(uid.substring(0,2));
        if(first.length() == 1){
            first = "0"+first;
        }
        String second = ConvertUtil.to_SixtyTwo(uid.substring(2,9));
        if(second.length() == 1){
            second = "000"+second;
        }else if(second.length() == 2){
            second = "00"+second;
        }else if(second.length() == 3){
            second = "0"+second;
        }

        String third = ConvertUtil.to_SixtyTwo(uid.substring(9,16));
        if(third.length() == 1){
            third = "000"+third;
        }else if(third.length() == 2){
            third = "00"+third;
        }else if(third.length() == 3){
            third = "0"+third;
        }
        return first+second+third;
    }

    //处理100万+的情况
    private static String handleEndChar(String transfer) {
        if(transfer.endsWith("万+")){
            String s = transfer.split("万+")[0];
            return s+"0000";
        }
        return transfer;
    }

/*    //mid进制转换
    private static String getUrlFromUid(String uid) {
        //转换uid 注意 我们需要判断是否需要在最开始追加0
        String first = ConvertUtil.to_SixtyTwo(uid.substring(0,2));
        if(first.length() == 1){
            first = "0"+first;
        }
        String second = ConvertUtil.to_SixtyTwo(uid.substring(2,9));
        if(second.length() == 1){
            second = "000"+second;
        }else if(second.length() == 2){
            second = "00"+second;
        }else if(second.length() == 3){
            second = "0"+second;
        }

        String third = ConvertUtil.to_SixtyTwo(uid.substring(9,16));
        if(third.length() == 1){
            third = "000"+third;
        }else if(third.length() == 2){
            third = "00"+third;
        }else if(third.length() == 3){
            third = "0"+third;
        }
        return first+second+third;
    }*/
}
