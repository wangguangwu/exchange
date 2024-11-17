package com.wangguangwu.exchangeordercore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangguangwu
 */
@SpringBootApplication(scanBasePackages = "com.wangguangwu")
@MapperScan("com.wangguangwu.exchange.mapper")
public class ExchangeOrderCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeOrderCoreApplication.class, args);
    }

}
