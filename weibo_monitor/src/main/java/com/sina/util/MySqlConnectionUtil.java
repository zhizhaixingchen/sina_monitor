package com.sina.util;

import java.sql.*;

//连接数据库 进行一些简单的操作
public class MySqlConnectionUtil {
    private static Connection connection;

    private MySqlConnectionUtil(){};

    private static void init(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/m_quartz?serverTimezone=Asia/Shanghai", "root", "123456");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static Connection getConnection(){
        if (connection == null){
            init();
        }
        return connection;
    }

    //从mysql中取出随机记录
    public static String getHost(){
        String str = "";
        //单例模式实例化connection
        Connection connection = MySqlConnectionUtil.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from finalproxy order by RAND() LIMIT 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            str = resultSet.getString(1)+":"+resultSet.getInt(2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return str;
    }
}
