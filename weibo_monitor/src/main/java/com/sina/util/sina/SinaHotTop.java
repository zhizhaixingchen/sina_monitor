package com.sina.util.sina;

import com.sina.pojo.SinaTopBean;
import com.sina.util.AnsjWord;
import com.sina.util.EmotionUtil;
import org.ansj.splitWord.analysis.NlpAnalysis;
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
import java.util.ArrayList;
import java.util.List;

public class SinaHotTop {

    /*
     * 从网页获取热度数据主方法
     * 1.对HttpUrlConnection进行初始化
     * 2.读取网页内容
     * 3.对网页内存进行数据处理，并返回List结果
     * 4.return resultList
     *   return null;
     * */
    public static void main(String[] args) {
        System.out.println(getSinaHotTop());
    }
    public static List<SinaTopBean> getSinaHotTop(){
        //初始化
        HttpURLConnection urlConn = urlConn_init();
        //获取网页源数据
        String word = htmlTextResponse(urlConn);
        //进行数据处理
        if(word.length() == 0){
            return null;
        }
        //对word进行数据清洗
        List<SinaTopBean> hotFromInet = handle_word(word);
        return hotFromInet;
    }
    //初始化urlConnection
    private static HttpURLConnection urlConn_init(){
        String target = "https://s.weibo.com/top/summary";
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(target);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConn;
    }

    /*
     * 通过urlConnection 获取网页结果
     * return 结果
     * return ""
     * */
    private static String htmlTextResponse(HttpURLConnection urlConn) {
        try {
            //获取返回结果code
            int responseCode = urlConn.getResponseCode();
            if(responseCode == 200){
                //获取输入流并读取内容
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = "";
                String word = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    word+=line;
                }
                br.close();
                return word;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*对获取到的html界面进行处理
     * Jsoup对获取的网页进行处理
     * 构造对象，将对象添加进List中
     * 返回list
     * */
    private static List<SinaTopBean> handle_word(String word) {
        List<SinaTopBean> list = new ArrayList<>();
        //情感一次传输，批量判断
        String[] emotion_str = new String[51];
        //计数器
        int count = 0;
        Document document = Jsoup.parseBodyFragment(word);
        Elements tbody = document.getElementsByTag("tbody");
        Elements tr_list = tbody.get(0).children();

        //先对第一条数据进行处理(新浪微博推荐热度数据)
        Elements first_tr_list = tr_list.get(0).children();
        //超链接部分
        Element first_link = first_tr_list.get(1);
        //超链接
        String td_link = "https://s.weibo.com/"+first_link.child(0).attr("href");
        //内容
        String td_content = first_link.child(0).text();

        //获取当前时间
        long current_date = System.currentTimeMillis();

        //判断文本情感   对于单次刷新不判断文本情感    在存储至数据库时判断文本情感
        //double fir_emotion = EmotionUtil.getWordEmotion(td_content);
        emotion_str[count++] = td_content;
        SinaTopBean firstBean = new SinaTopBean(current_date++,0,td_content,0,0,td_link);
        //暂时关闭词云
/*        List<String> wordtest = new ArrayList<>();
        wordtest.add("test");
        firstBean.setWords(wordtest);*/
        firstBean.setWords(AnsjWord.nlpWord(td_content));
        list.add(firstBean);

        //对其它50条数据进行处理
        Elements tr_other = tr_list.next();
        for (Element tr : tr_other) {
            Elements td = tr.children();
            //当前排名
            String stage_str = td.get(0).text();
            int stage = Integer.parseInt(stage_str);

            //超链接部分
            Element link_td = td.get(1);
            String link = "https://s.weibo.com/";
            link += link_td.child(0).attr("href");
            //内容
            String content = link_td.child(0).text();
            //热度
            String hot_str = link_td.child(1).text();
            long hot = Long.parseLong(hot_str);

            emotion_str[count++] = content;
            SinaTopBean bean = new SinaTopBean(current_date++,stage,content,hot,0,link);
            //词云
            bean.setWords(AnsjWord.nlpWord(content));
            //bean.setWords(wordtest);
            list.add(bean);
        }

        //暂不使用  开发完成取消注释
        //对情感进行批量判断
/*        Double[] emotion_result = EmotionUtil.getWordEmotionList(emotion_str);
        //再追加
        for(int i = 0;i<emotion_result.length;i++){
            list.get(i).setEmotion_point(emotion_result[i]);
        }*/
        return list;
    }

}
