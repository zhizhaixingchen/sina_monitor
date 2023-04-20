package com.sina.util;

import com.alibaba.fastjson.JSON;
import com.sina.pojo.BlogMysql;
import com.sina.pojo.SinaGroupMonitor;
import com.sina.pojo.SinaUserMonitor;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class test {
    //计数
    private static int count = 0;
    //存放中间结果
    private static int[] medium = new int[2];

    public static void main(String []args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
/*        SinaUserMonitor s = new SinaUserMonitor(1,"test",1111,1111,new SinaGroupMonitor(211,"ceshizu",1234,12,11));
        String s1 = JSON.toJSONString(s);
        SinaUserMonitor monitor = JSON.parseObject(s1,SinaUserMonitor.class);
        System.out.println(monitor);*/
        //String str = "{groupMonitor:{g_id:0,g_name:\"默认\",g_time:0,g_user_numcur:0,g_user_nummax:20},u_blognum:12,u_hot:9999,u_id:10086,u_name:\"中国移动\"}";
        //SinaUserMonitor userMonitor = (SinaUserMonitor) JSON.parse(str);
        //System.out.println(userMonitor);
/*        String str = "{\"uid\":1642591402,\"mid\":4624352651512953,\"name\":\"新浪娱乐\",\"time\":55}";
        BlogMysql blogMysql = JSON.parseObject(str, BlogMysql.class);
        System.out.println(blogMysql);*/
/*        String x="123456";
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(x.getBytes("UTF8"));
        byte s[ ]=m.digest( );
        String result="";
        for (int i=0; i<s.length;i++){
            result+=Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
        }*/
/*        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        System.out.println(now);*/
        //一小时之前
        try{
            int i = 5/0;
        }catch(ArithmeticException e){
            e.printStackTrace();
        }
        System.out.println("hello");

    }

    public static int[] calc(int[] defaultNum){
        count++;
        int a = defaultNum[0];
        int b = defaultNum[1];
        //随机选择两个数字
        int c = a+(int)(Math.random()*(b-a));
        int d = a+(int)(Math.random()*(b-a));
        //相减绝对值
        int re = Math.abs(d-c);
        if(judgePrime(c)&&judgePrime(d)&&re == 2){
            medium[0] = c;
            medium[1] = d;
            return medium;
        }else{
            medium[0] = a;
            medium[1] = b+1;
            return calc(medium);
        }
    }

    //判断一个数是不是质数
    public static boolean judgePrime(int num){
        if(num < 2){
            return false;
        }
        if(num == 2 || num == 3){
            return true;
        }else{
            int a = (int)Math.sqrt(num);
            for(int i = 2; i <= a ; i++){
                if(num % i == 0){
                    return false;
                }
            }
            return true;
        }
    }
}



























class MyRunnable implements Runnable{
    private int a;
    @Override
    public void run() {
        while(true){

            try {
                Thread.sleep(2);
                System.out.println(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}


class Ansj {
    public static List<String> nlpWord(String str) {
        List<String> list = new ArrayList<>();
        //只关注这些词性的词
        Set<String> expectedNature = new HashSet<String>() {{
            add("n");add("nr");;add("ns");add("mq");add("q");add("j");
            add("nt");add("nz");add("nw");add("ng");add("wh");add("en");
        }};
        Result result = NlpAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
        System.out.println(result.getTerms());

        List<Term> terms = result.getTerms(); //拿到terms

        System.out.println(terms.size());

        for(int i=0; i<terms.size(); i++) {
            String word = terms.get(i).getName(); //拿到词
            String natureStr = terms.get(i).getNatureStr(); //拿到词性
            if(expectedNature.contains(natureStr)) {
                //不需要单个组成的词语
                if(word.length()!=1){
                    list.add(word);
                }
            }
        }
        System.out.println("list = "+list);
        return list;
    }
}