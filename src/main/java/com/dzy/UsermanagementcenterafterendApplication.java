package com.dzy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.dzy.mapper")
public class UsermanagementcenterafterendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsermanagementcenterafterendApplication.class, args);
    }

}
