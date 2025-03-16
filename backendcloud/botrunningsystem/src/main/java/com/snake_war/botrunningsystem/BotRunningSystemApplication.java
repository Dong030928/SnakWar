package com.snake_war.botrunningsystem;

import com.snake_war.botrunningsystem.service.impl.BotRunningServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args) {
        BotRunningServiceImpl.botPool.start();

        SpringApplication.run(BotRunningSystemApplication.class, args);
    }
}