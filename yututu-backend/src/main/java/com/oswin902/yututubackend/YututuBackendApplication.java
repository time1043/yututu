package com.oswin902.yututubackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.oswin902.yututubackend.mapper")  // mybatis-plus
@EnableAspectJAutoProxy(exposeProxy = true) // spring-aop  AopContext.currentProxy()
public class YututuBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YututuBackendApplication.class, args);
    }

}
