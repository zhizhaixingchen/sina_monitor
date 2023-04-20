package com.sina.util.sina;

import com.sina.pojo.SinaTopBean;
import com.sina.pojo.SinaTopicBean;
import com.sina.pojo.SingleBlog;
import com.sina.util.ConvertUtil;
import com.sina.util.EmotionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//从topic中获取singleBlog
public class SinaCommonTopic {
    public static SinaTopicBean getCommonTopic(String keyword,SinaTopBean sinaTopBean){
        //初始化爬虫
        HttpURLConnection urlConnection = urlConn_init(keyword);
        //返回Html源数据
        String data = getOriginalHtmlData(urlConnection);
        //处理数据
        SinaTopicBean sinaTopicBean = format_data(data,sinaTopBean);
        sinaTopicBean.setSinaTopBean(sinaTopBean);
        return sinaTopicBean;
    }
    //初始化爬虫工具
    private static HttpURLConnection urlConn_init(String keyWord){
        try {
            keyWord = URLEncoder.encode(keyWord,"utf-8");
            String target = "https://s.weibo.com/weibo?q=%23"+keyWord+"%23&xsort=hot&Refer=hotmore";
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //获取网页原始数据
    private static String getOriginalHtmlData(HttpURLConnection urlConn) {
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
    private static SinaTopicBean format_data(String data, SinaTopBean sinaTopBean) {
        Document parse = Jsoup.parse(data);
        Element body = parse.body();
        //处理第一部分
        SinaTopicBean singBeanPartOne = handle_head(body);
        //处理第二部分
        Map<String,Object> map = hand_singleBlog(body,singBeanPartOne);
        //进行情感处理 先注释
        //SinaTopicBean finalbean = emotion_handle(map,sinaTopBean);
        return (SinaTopicBean) map.get("sinaTopicBean");
    }

    //处理第一部分
    //照片      标题名称        阅读      讨论
    private static SinaTopicBean handle_head(Element body) {
        SinaTopicBean sinaTopicBean = new SinaTopicBean();
        Elements basic_01 = body.getElementsByClass("card-topic-a");
        //检索到指定话题
        if (basic_01.size() >= 1) {
            //获取话题图片
            Element img = basic_01.get(0).child(0);
            String imgUrl = img.attr("src");
            //获取话题
            Element context = basic_01.get(0).child(1).child(0).child(0);
            String title = context.wholeText().replace("#",""); //删除前后##
            //获取阅读量
            Element read_element = basic_01.get(0).child(1).child(1).child(0);
            String read_count = read_element.text().split("阅读")[1];
            //获取讨论量
            Element discuss_element = basic_01.get(0).child(1).child(1).child(1);
            String discuss_count = discuss_element.text().split("讨论")[1];

            sinaTopicBean.setCurrent_date(System.currentTimeMillis());
            sinaTopicBean.setImgUrl(imgUrl);
            sinaTopicBean.setDiscuss(discuss_count);
            sinaTopicBean.setKeyword(title);
            sinaTopicBean.setReadNum(read_count);
        }
        return sinaTopicBean;
    }

    //处理第二部分
    //基本信息2     热门微博相关
    //参数                                    body            当前话题
    //返回Map        1.emotion_judge 将要批量处理的情感句子         2.singleBlogList  单微博列表
    private static Map<String,Object> hand_singleBlog(Element body, SinaTopicBean sinaTopicBean) {
        Map<String,Object> map = new HashMap<>();
        Elements basic_02 = body.getElementsByClass("m-con-l");
        //将待检测的话封装在emotion_judge之中，集体送检
        List<String> emotion_judge = new ArrayList<>();

        //检索进指定目录
        if(basic_02.size() == 1){

            Element raw_message = basic_02.get(0).child(1);
            Elements rawSinaVblogList = raw_message.getElementsByClass("card-wrap");
            List<SingleBlog> singleBlogList = new ArrayList<>();
            for (Element element : rawSinaVblogList) {
                //mid获取
                String mid_str = element.attr("mid");
                long mid = Long.parseLong(mid_str);

                //头像获取
                String imgUrl = element.getElementsByClass("avator").get(0).getElementsByTag("img").get(0).attr("src");
                //内容获取 和 用户名获取
                Elements raw_context = element.getElementsByAttributeValue("node-type","feed_list_content_full");
                //若是短文本，则不存在上述属性
                if(raw_context.size() == 0){
                    raw_context = element.getElementsByAttributeValue("node-type","feed_list_content");
                }

                Element context_name = raw_context.get(0);
                //获取用户名
                String userName = context_name.attr("nick-name");
                //获取文本内容
                String context = context_name.wholeText();
                //去掉空格
                context = context.replace(" ", "");
                //删除最后的收起全文字样
                if(context.contains("收起全文d")){
                    context = context.split("收起全文d")[0];
                }
                //添加待检测的话
                emotion_judge.add(context);

                //获取发表时间
                Elements from = element.getElementsByClass("from");
                String pub_time = from.get(0).child(0).text();

                //获取最后的转发，评论，点赞
                Element act = element.getElementsByClass("card-act").get(0);
                String transfer_str = act.child(0).child(1).text();
                //排除0转发的问题
                transfer_str = transfer_str.replace(" ","");
                transfer_str = transfer_str.substring(2);
                if(transfer_str.length() == 0){
                    transfer_str= "0";
                }
                long transfer = Long.parseLong(transfer_str);
                //在转发位置获取uid
                String rawUrl = act.child(0).child(1).child(0).attr("action-data");
                Pattern p = Pattern.compile("&uid=(\\d*)&");
                Matcher matcher = p.matcher(rawUrl);
                String uid = "";
                if(matcher.find()){
                    uid = matcher.group(1);
                }
                //进入该微博链接
                String mid_url = getUrlFromUid(String.valueOf(mid));
                String rawLink = "https://weibo.com/"+uid+"/"+mid_url+"?wvr=6";

                //排除0评论的问题
                String comments_str = act.child(0).child(2).text();
                comments_str = comments_str.replace(" ","");
                comments_str = comments_str.substring(2);
                if(comments_str.length() == 0){
                    comments_str= "0";
                }
                long comments = Long.parseLong(comments_str);

                String like_str = act.child(0).child(3).child(0).text();
                like_str = like_str.replace(" ","");
                if(like_str.length() == 0){
                    like_str= "0";
                }
                long like = Long.parseLong(like_str);

                SingleBlog singleBlog = new SingleBlog(mid,userName,uid,imgUrl,context,pub_time,transfer,comments,like);
                //影响因子，计算公式 点赞*1+评论*5+转发*10
                long hot = like+comments*5+transfer*10;
                singleBlog.setHot(hot);
                singleBlog.setMyUrl(rawLink);
                singleBlogList.add(singleBlog);
            }
            sinaTopicBean.setHot_blog(singleBlogList);
        }
        map.put("emotion_judge",emotion_judge);
        map.put("sinaTopicBean",sinaTopicBean);
        return map;
    }

    //情感分析
    private static SinaTopicBean emotion_handle(Map<String, Object> map,SinaTopBean sinaTopBean) {
        SinaTopicBean sinaTopicBean = (SinaTopicBean) map.get("sinaTopicBean");
        List<String> emotion_judge = (List<String>) map.get("emotion_judge");
        //如果有热门微博
        if(emotion_judge.size()!=0){
            List<SingleBlog> hotBlogList = sinaTopicBean.getHot_blog();
            //将topic添加至最后一个
            //判断是否需要计算话题情感因子   如果sinaTopBean == null 则说明，没有上热门
            if(sinaTopBean == null){
                emotion_judge.add(sinaTopicBean.getKeyword());
            }

            //计算情感因子
            String[] a = new String[emotion_judge.size()];
            emotion_judge.toArray(a);
            Double[] wordEmotionArray = EmotionUtil.getWordEmotionList(a);  //得到的结果为[微博1,微博2,微博3,微博4,...,topic]

            //计算影响影子和总情感分数
            //总影响因子
            long total_influence = 0;
            //分子和话题情感分*微博第一影响因子+相应微博情感分*影响因子
            double total_fm = 0;

            //将获取的情感因子插入对应的singleBlog中，并计算总情感因子
            for(int i = 0;i<hotBlogList.size();i++){
                long influence = hotBlogList.get(i).getHot();
                //计算情感因子
                double emotion = wordEmotionArray[i];
                //插入情感因子
                hotBlogList.get(i).setEmotion(emotion);
                //两值累计
                total_fm += emotion*influence;
                total_influence += influence;
                System.out.println("emotion = "+emotion+"\tword = "+hotBlogList.get(i).getContext());
            }

            //计算话题情感
            if(sinaTopBean == null){
                //添加话题情感
                sinaTopicBean.setFinalEmotionStage(wordEmotionArray[hotBlogList.size()]);
                //话题情感*当前第一条微博影响因子
                total_fm += wordEmotionArray[hotBlogList.size()]*hotBlogList.get(0).getHot();
                total_influence += hotBlogList.get(0).getHot();
            }
            //将情感信息进行插入
            double total_emotion = total_fm/total_influence;
            sinaTopicBean.setFinalEmotionStage(total_emotion);
            //将微博信息插入
            sinaTopicBean.setHot_blog(hotBlogList);
        }else{
            //没有热门微博        获取话题情感 并添加进去
            String topic = sinaTopicBean.getKeyword();
            double wordEmotion = EmotionUtil.getWordEmotion(topic);
            sinaTopicBean.setFinalEmotionStage(wordEmotion);
        }
        return sinaTopicBean;
    }

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
    }
}
