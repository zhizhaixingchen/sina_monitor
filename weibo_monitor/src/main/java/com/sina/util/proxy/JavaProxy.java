package com.sina.util.proxy;



import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaProxy {
    public static Connection connection = null;
    public static void main(String[] args) {
        try {
            getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

/*        for(int i = 2;i<300;i++){
            new IpProxyGet(i,connection).start();
        }*/

        //host = 179.108.123.210port = 51314
        //host = 80.93.213.213port = 3136
        //host = 5.16.0.77port = 1256

        //ip测试1
        getTest1();
        //ip测试2
        //159.65.69.186	9300
        //getTest2();
    }

    //IP测试1
    private static void getTest1(){
        String sql = "select host,port from proxy";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet execute = preparedStatement.executeQuery();
            while(execute.next()){
                String host = execute.getString(1);
                int port = execute.getInt(2);
                new IpTest(host,port,connection).start();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //ip测试2
    private static void getTest2(){
        String sql = "select host,port from finalproxy";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet execute = preparedStatement.executeQuery();
            while(execute.next()){
                String host = execute.getString(1);
                int port = execute.getInt(2);
                new IpTest2(host,port,connection).start();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        //3、获取数据库的连接对象
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/m_quartz?serverTimezone=Asia/Shanghai", "root", "123456");
    }
}
//ip获取
class IpProxyGet extends Thread{
    private int i;
    private List<String> list;
    private Connection connection;
    public IpProxyGet(int i,Connection connection){
        this.i = i;
        this.connection = connection;
    }

    @Override
    public void run() {
        list = crabProxy(i);
        try {
            insertData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertData() throws SQLException {
        for (String s : list) {
            String[] split = s.split(":");
            String sql = "REPLACE INTO proxy(host,port) " +
                    "VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,split[0]);
            preparedStatement.setInt(2, Integer.parseInt(split[1]));
            preparedStatement.executeUpdate();
        }
    }

    private static List<String> crabProxy(int i) {
        List<String> list = new ArrayList<>();
        String target = "http://www.xiladaili.com/https/"+i;
        String word = "";
        try {
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
            urlConn.setRequestProperty("cookie","csrftoken=eKh1hVf9K8kUxjh9cDFg30fYiqKqfCgyEPPxdNWAmtISuV7o9spLN6wjowLqTbRD");
            System.out.println(urlConn.getResponseCode());
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
        String patterns = "<tr><td>(.*?)</td>";
        Pattern p = Pattern.compile(patterns);
        Matcher matcher = p.matcher(word);
        while(matcher.find()){
            String g1 = matcher.group(1);
            list.add(g1);
            System.out.println("--->"+g1);
        }
        return list;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}

//ip测试
class IpTest extends Thread{
    private String host;
    private int port;
    private Connection connection;
    public IpTest(String host,int port,Connection connection){
        this.host = host;
        this.port = port;
        this.connection = connection;
    }
    @Override
    public void run() {
        try {
            crabSina(host,port);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void crabSina(String host,int port) throws SQLException {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,port));
        String target = "https://weibo.com/1618051664/K9ZAr5Blu?type=comment";
        try {
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(proxy);
            urlConn.setConnectTimeout(3000);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
            urlConn.setRequestProperty("cookie","SCF=Au2eJQkyRy3oY5E_toowetXk7pnv7v9F1NnzAlpOcwPzAUrw8BqNpHS_aRlQnPJJWME6mjd_93QFlgRdxZW6q9A.; UOR=souky.eol.cn,widget.weibo.com,cn.bing.com; SINAGLOBAL=8209711189153.645.1581563099315; ULV=1616203893344:92:80:41:2501337776855.4634.1616203893341:1616146484708; webim_unReadCount=%7B%22time%22%3A1616147634920%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5ME1Qa7AH5CbcsV.zi1T_N; ALF=1647682480; SUB=_2AkMXCBSof8NxqwFRmPgcyWLiZYR1zQvEieKhVOVzJRMxHRl-yT9kqk87tRB6PIg6R2wzUr862dWmDkZM5YEb5M_vD5kM; login_sid_t=3ee402677939fc8f0559de10e2ffe109; cross_origin_proto=SSL; _s_tentry=cn.bing.com; Apache=2501337776855.4634.1616203893341; wb_view_log=1536*8641.25; WBtopGlobal_register_version=2021032010");
            System.out.println(urlConn.getResponseCode());
            if(urlConn.getResponseCode() == 200){
                System.out.println("host = "+host +"port = "+port);
                String sql = "REPLACE INTO finalproxy(host,port) value(?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,host);
                preparedStatement.setInt(2,port);
                preparedStatement.executeUpdate();
            }
        } catch (SocketTimeoutException | SocketException | EOFException | SSLHandshakeException e){
            //删除与否无所谓
            String sql = "DELETE FROM proxy where host = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,host);
            preparedStatement.execute();
            System.out.println(host+":"+port+"被删除，原因："+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}

class IpTest2 extends Thread{
    private String host;
    private int port;
    private Connection connection;
    public IpTest2(String host,int port,Connection connection){
        this.host = host;
        this.port = port;
        this.connection = connection;
    }
    @Override
    public void run() {
        try {
            crabSina(host,port);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void crabSina(String host,int port) throws SQLException {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,port));
        String target = "https://weibo.com/1618051664/K9ZAr5Blu?type=comment";
        try {
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(proxy);
            urlConn.setConnectTimeout(3000);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConn.setRequestProperty("Charset", "utf-8");
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
            urlConn.setRequestProperty("Accept", "*/*");
            urlConn.setRequestProperty("Accept-Language", "zh-cn");
            urlConn.setRequestProperty("cookie","SCF=Au2eJQkyRy3oY5E_toowetXk7pnv7v9F1NnzAlpOcwPzAUrw8BqNpHS_aRlQnPJJWME6mjd_93QFlgRdxZW6q9A.; UOR=souky.eol.cn,widget.weibo.com,cn.bing.com; SINAGLOBAL=8209711189153.645.1581563099315; ULV=1616203893344:92:80:41:2501337776855.4634.1616203893341:1616146484708; webim_unReadCount=%7B%22time%22%3A1616147634920%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A0%2C%22msgbox%22%3A0%7D; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5ME1Qa7AH5CbcsV.zi1T_N; ALF=1647682480; SUB=_2AkMXCBSof8NxqwFRmPgcyWLiZYR1zQvEieKhVOVzJRMxHRl-yT9kqk87tRB6PIg6R2wzUr862dWmDkZM5YEb5M_vD5kM; login_sid_t=3ee402677939fc8f0559de10e2ffe109; cross_origin_proto=SSL; _s_tentry=cn.bing.com; Apache=2501337776855.4634.1616203893341; wb_view_log=1536*8641.25; WBtopGlobal_register_version=2021032010");
            System.out.println(urlConn.getResponseCode());
            if(urlConn.getResponseCode() == 200){
                System.out.println("host = "+host +"port = "+port);
            }else{
                String sql = "DELETE FROM finalproxy where host = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,host);
                preparedStatement.execute();
                System.out.println(host+":"+port+"被删除");
            }
        } catch (SocketTimeoutException | SocketException | EOFException | SSLHandshakeException e){
            String sql = "DELETE FROM finalproxy where host = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,host);
            preparedStatement.execute();
            System.out.println(host+":"+port+"被删除，原因："+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}


