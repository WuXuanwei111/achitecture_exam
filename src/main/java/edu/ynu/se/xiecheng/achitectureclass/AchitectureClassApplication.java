package edu.ynu.se.xiecheng.achitectureclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AchitectureClassApplication {
    public static void main(String[] args) {
        SpringApplication.run(AchitectureClassApplication.class, args);
    }
}
