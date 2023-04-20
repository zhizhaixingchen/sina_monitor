package com.sina.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaCookie {
    public static void main(String[] args) {
        String cookie = getCookie();
        System.out.println(cookie);
    }
    public static String getCookie(){
        String str = "";
        String part1 = getPart1();
        String s = getpart2(part1);
        return s;
    }

    public static String getPart1(){
        String tid = "";
        String target = "https://passport.weibo.com/visitor/genvisitor?cb=gen_callback";
        try {
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            if(urlConn.getResponseCode() == 200){
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = "";
                String word = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    word+=line;
                }
                System.out.println(word);
                Pattern pattern = Pattern.compile("\"tid\":\"(.*)\",");
                Matcher matcher = pattern.matcher(word);
                if(matcher.find()){
                    tid = matcher.group(1);
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tid;
    }
    //https://passport.weibo.com/visitor/visitor?a=incarnate&t=接口1里的tid&w=3&c=100&cb=cross_domain&from=weibo（返回sub和subp）
    public static String getpart2(String tid){
        String cookie = "";
        String target = "https://passport.weibo.com/visitor/visitor?a=incarnate&t="+tid+"&w=3&c=100&cb=cross_domain&from=weibo";
        System.out.println(target);
        try {
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Host","passport.weibo.com");
            urlConn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
            urlConn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
            urlConn.setRequestProperty("Accept-Encoding","gzip,deflate,br");
            if(urlConn.getResponseCode() == 200){
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String line = "";
                String word = "";
                while ((line = br.readLine()) != null) { // 通过循环逐行读取输入流中的内容
                    word+=line;
                }
                System.out.println(word);
                String sub = "";
                Pattern pattern1 = Pattern.compile("\"sub\":\"(.*)\",\"");
                Matcher matcher1 = pattern1.matcher(word);
                if(matcher1.find()){
                    sub = matcher1.group(1);
                }

                String subp = "";
                Pattern pattern2 = Pattern.compile("\"subp\":\"(.*)\"}}");
                Matcher matcher2 = pattern2.matcher(word);
                if(matcher2.find()){
                    subp = matcher2.group(1);
                }
                cookie = "SUB="+sub+"; SUBP="+subp;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cookie;
    }
}
