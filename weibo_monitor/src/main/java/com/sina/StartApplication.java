package com.sina;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;

@SpringBootApplication
@MapperScan(value = "com.sina.dao.mapper",annotationClass = Repository.class)
public class StartApplication {
    public static void main(String args[]){
        //启动SpringBoot应用
        SpringApplication.run(StartApplication.class,args);
    }

}
