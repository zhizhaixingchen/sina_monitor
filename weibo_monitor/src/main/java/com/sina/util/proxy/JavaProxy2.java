package com.sina.util.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sina.util.MySqlConnectionUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaProxy2 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
    }
}


class CrabIp extends Thread{

    @Override
    public void run() {
        crabProxy();
    }


    private static List<String> crabProxy() {
        List<String> list = new ArrayList<>();
        String target = "https://ip.ihuan.me/tqdl.html";
        String word = "";
        try {
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setConnectTimeout(3000);
            urlConn.setReadTimeout(10000);
            //设置是否向httpURLConnection输出
            urlConn.setDoOutput(true);
            //设置是否从httpURLConnection读入
            urlConn.setDoInput(true);
            //设置是否使用缓存
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Host","ip.ihuan.me");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
            urlConn.setRequestProperty("user-Agent"," Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0");
            urlConn.setRequestProperty("cookie","__cfduid=df36a847bf4bb6a3dd45f554f0436c5591617842003; statistics=66ec6a80fa7013fddb1e67d9da0622a8");
            //处理post
            OutputStream outputStream = urlConn.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("num",100);
            jsonObject.put("type",1);
            jsonObject.put("key","fedd9cd3846cde3bfac9bd2ec6daf2f8");
            outputStreamWriter.write(jsonObject.toJSONString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            if(urlConn.getResponseCode() == 200){
                //获取输入流并读取内容
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    line = line.replace(" ","");
                    word+=line;
                }
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("word = "+word);
        /*String patterns = "<tr><td>(.*?)</td>";
        Pattern p = Pattern.compile(patterns);
        Matcher matcher = p.matcher(word);
        while(matcher.find()){
            String g1 = matcher.group(1);
            list.add(g1);
            System.out.println("--->"+g1);
        }*/
        return list;
    }
}
