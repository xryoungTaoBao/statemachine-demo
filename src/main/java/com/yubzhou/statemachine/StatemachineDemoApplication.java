package com.yubzhou.statemachine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.yubzhou.statemachine.order.mapper")
@EnableScheduling
public class StatemachineDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatemachineDemoApplication.class, args);
    }
}
