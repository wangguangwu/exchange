package com.wangguangwu.exchangeusercore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangguangwu
 */
@SpringBootApplication(scanBasePackages = "com.wangguangwu")
@MapperScan("com.wangguangwu.exchange.mapper")
public class ExchangeUserCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeUserCoreApplication.class, args);
    }

}
