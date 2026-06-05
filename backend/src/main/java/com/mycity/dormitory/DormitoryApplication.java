package com.mycity.dormitory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动入口
 */
@SpringBootApplication
@MapperScan("com.mycity.dormitory.mapper")  // 扫描 Mapper 接口包
public class DormitoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryApplication.class, args);  // 启动 Spring Boot
    }
}
