package com.sina.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//进制转换 用于查看新浪微博
public class ConvertUtil {
    private static final Map<Character,Integer> BASIC = new HashMap<Character, Integer>();
    private static final int SCALE = 62;
    private static final String BASIC_2 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args) {
        String s = to_SixtyTwo("46");
        String s1 = to_SixtyTwo("1675438");
        String s2 = to_SixtyTwo("9846714");
        System.out.println(s+"\t"+s1+"\t"+s2);
    }
    static {
        BASIC.put('0',0);
        BASIC.put('1',1);
        BASIC.put('2',2);
        BASIC.put('3',3);
        BASIC.put('4',4);
        BASIC.put('5',5);
        BASIC.put('6',6);
        BASIC.put('7',7);
        BASIC.put('8',8);
        BASIC.put('9',9);
        BASIC.put('a',10);
        BASIC.put('b',11);
        BASIC.put('c',12);
        BASIC.put('d',13);
        BASIC.put('e',14);
        BASIC.put('f',15);
        BASIC.put('g',16);
        BASIC.put('h',17);
        BASIC.put('i',18);
        BASIC.put('j',19);
        BASIC.put('k',20);
        BASIC.put('l',21);
        BASIC.put('m',22);
        BASIC.put('n',23);
        BASIC.put('o',24);
        BASIC.put('p',25);
        BASIC.put('q',26);
        BASIC.put('r',27);
        BASIC.put('s',28);
        BASIC.put('t',29);
        BASIC.put('u',30);
        BASIC.put('v',31);
        BASIC.put('w',32);
        BASIC.put('x',33);
        BASIC.put('y',34);
        BASIC.put('z',35);
        BASIC.put('A',36);
        BASIC.put('B',37);
        BASIC.put('C',38);
        BASIC.put('D',39);
        BASIC.put('E',40);
        BASIC.put('F',41);
        BASIC.put('G',42);
        BASIC.put('H',43);
        BASIC.put('I',44);
        BASIC.put('J',45);
        BASIC.put('K',46);
        BASIC.put('L',47);
        BASIC.put('M',48);
        BASIC.put('N',49);
        BASIC.put('O',50);
        BASIC.put('P',51);
        BASIC.put('Q',52);
        BASIC.put('R',53);
        BASIC.put('S',54);
        BASIC.put('T',55);
        BASIC.put('U',56);
        BASIC.put('V',57);
        BASIC.put('T',58);
        BASIC.put('X',59);
        BASIC.put('Y',60);
        BASIC.put('Z',61);
    }
//4616754389846714
//K71RcFjzY
    //62进制转换为10进制
    public static String to_ten(String word){
        long num = 0;
        int times = 0;
        for(int i = word.length()-1;i>=0;i--){
            char c = word.charAt(i);
            Integer in = BASIC.get(c);
            num += in*Math.pow(62,times++);
        }
        return String.valueOf(num);
    }

    //10进制转62进制
    public static String to_SixtyTwo(String uid){
        String str = "";
        StringBuffer sb = new StringBuffer();
        long num = Long.parseLong(uid);
        do{
            int remainder = 0;
            remainder = (int) (num%SCALE);
            sb.append(BASIC_2.charAt(remainder));
            num = num/SCALE;
        }while (num!=0);
        str = sb.reverse().toString();
        return str;
    }

    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            // group 6728
            String group = matcher.group(2);
            // ch:'木' 26408
            ch = (char) Integer.parseInt(group, 16);
            // group1 \u6728
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }
}
