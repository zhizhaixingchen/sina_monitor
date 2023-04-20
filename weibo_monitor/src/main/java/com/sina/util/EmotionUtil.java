package com.sina.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

//情感分析处理
public class EmotionUtil {
    //调用python flask框架 判断语句情感
    /*
    * 判断文本情感位置
    * 存储数据库
    * 展示详细信息
    *
    * */
    public static double getWordEmotion(String word){
        try {
            word = URLEncoder.encode(word,"utf-8");
            URL url = new URL("http://127.0.0.1:5000/judge_emotion?sentence="+word);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 200){
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String word1 = "";
                String line = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    word1+=line;
                }
                //关闭输入流
                br.close();
                //转换为Json对象
                JSONObject jsonObject = JSON.parseObject(word1);
                String positive_str = jsonObject.get("positive").toString();
                double positive = Double.parseDouble(positive_str);
                return positive;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果没有响应 返回0
        return 0;
    }


    //获取多条信息情感
    public static Double[] getWordEmotionList(String[] emotion_str){
        Double[] emotion_result = new Double[51];
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:5000/judgeEmotionList");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            //参数形式key=value&key2=value2
            String param = "emotion_str="+ Arrays.asList(emotion_str);
            OutputStream outputStream = urlConn.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            outputStream.close();
            int responseCode = urlConn.getResponseCode();
            if(responseCode == 200){
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String word = "";
                String line = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    word+=line;
                }
                List<Double> doubles = JSON.parseArray(word,Double.class);
                emotion_result = doubles.toArray(new Double[doubles.size()]);
                //关闭输入流
                br.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return emotion_result;
    }
}
