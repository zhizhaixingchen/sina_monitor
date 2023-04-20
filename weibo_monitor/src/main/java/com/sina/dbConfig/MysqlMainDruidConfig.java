package com.sina.dbConfig;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
//对mysql进行配置
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, DataSourceCommonProperties.class})//将配置类注入到bean容器，使ConfigurationProperties注解类生效
public class MysqlMainDruidConfig {

    private DataSourceProperties dataSourceProperties;
    private DataSourceCommonProperties dataSourceCommonProperties;
    public MysqlMainDruidConfig(DataSourceProperties dataSourceProperties, DataSourceCommonProperties dataSourceCommonProperties) {
        this.dataSourceProperties = dataSourceProperties;
        this.dataSourceCommonProperties = dataSourceCommonProperties;
    }

    @Primary //标明为主数据源，只能标识一个主数据源，mybatis连接默认主数据源
    @Bean("mysqlDruidDataSource") //新建bean实例
    @Qualifier("mysqlDruidDataSource")//标识
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();

        //配置数据源属性
        datasource.setUrl(dataSourceProperties.getMysqlMain().get("url"));
        datasource.setUsername(dataSourceProperties.getMysqlMain().get("username"));
        datasource.setPassword(dataSourceProperties.getMysqlMain().get("password"));
        datasource.setDriverClassName(dataSourceProperties.getMysqlMain().get("driver-class-name"));

        //配置统一属性
        datasource.setInitialSize(dataSourceCommonProperties.getInitialSize());
        datasource.setMinIdle(dataSourceCommonProperties.getMinIdle());
        datasource.setMaxActive(dataSourceCommonProperties.getMaxActive());
        datasource.setMaxWait(dataSourceCommonProperties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(dataSourceCommonProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(dataSourceCommonProperties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(dataSourceCommonProperties.getValidationQuery());
        datasource.setTestWhileIdle(dataSourceCommonProperties.isTestWhileIdle());
        datasource.setTestOnBorrow(dataSourceCommonProperties.isTestOnBorrow());
        datasource.setTestOnReturn(dataSourceCommonProperties.isTestOnReturn());
        datasource.setPoolPreparedStatements(dataSourceCommonProperties.isPoolPreparedStatements());
        try {
            datasource.setFilters(dataSourceCommonProperties.getFilters());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datasource;
    }

}
