package com.lizi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lizi.mapper")
public class LiZiBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiZiBlogApplication.class,args);
    }
}
