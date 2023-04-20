package com.sina.dbConfig;

//获取yml中的数据
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = DataSourceProperties.DS, ignoreUnknownFields = false)
public class DataSourceProperties {
    final static String DS = "spring.datasource";

    private Map<String,String> mysqlMain;

    private Map<String,String> hive;

    private Map<String,String> commonConfig;


    public void setMysqlMain(Map<String, String> mysqlMain) {
        this.mysqlMain = mysqlMain;
    }

    public Map<String, String> getMysqlMain() {
        return mysqlMain;
    }

    public Map<String, String> getHive() {
        return hive;
    }

    public void setHive(Map<String, String> hive) {
        this.hive = hive;
    }

    public Map<String, String> getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(Map<String, String> commonConfig) {
        this.commonConfig = commonConfig;
    }
}