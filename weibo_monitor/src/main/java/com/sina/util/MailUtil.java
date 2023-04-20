package com.sina.util;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//邮件发送线程
public class MailUtil implements Runnable{
    //接收者
    private String mail;
    //标题
    private String title;
    //内容
    private String content;

    public MailUtil(String mail,String title,String content){
        this.mail = mail;
        this.title = title;
        this.content = content;
    }

    @Override
    public void run() {
        try {
            sendMail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //发送方法主体
    public void sendMail() throws MessagingException, javax.mail.MessagingException {
        if(mail != "" && !("".equals(mail))){
            //获取配置文件
            Map<String, String> config_map = getPropertisFromConfig();

            Properties props = System.getProperties(); // 获得系统属性配置，用于连接邮件服务器的参数配置
            props.setProperty("mail.smtp.host", config_map.get("host")); // 发送邮件的主机
            props.setProperty("mail.smtp.auth", "true");

            Session session = Session.getInstance(props, null);// 获得Session对象
            session.setDebug(true); // 设置是否显示debug信息,true 会在控制台显示相关信息

            /*
             * 创建邮件消息，发送邮件
             */
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config_map.get("from")));

            //指定收件人
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(mail, false));

            message.setSubject(title); // 邮件标题
            message.setText(content); // 邮件内容

            // 填写用户名 密码
            Transport.send(message, config_map.get("from"), config_map.get("key"));
        }
    }

    //获取配置文件信息    user_set.properties
    public static Map<String,String> getPropertisFromConfig(){
        Map<String,String> map = new HashMap<>();
        java.util.Properties properties = new java.util.Properties();
        InputStream in = MailUtil.class.getClassLoader().getResourceAsStream("mail.properties");
        // 使用properties对象加载输入流
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取key对应的value值
        String key = properties.getProperty("key");
        map.put("key",key);
        String from = properties.getProperty("from");
        map.put("from",from);
        String host = properties.getProperty("host");
        map.put("host",host);
        return map;
    }
}