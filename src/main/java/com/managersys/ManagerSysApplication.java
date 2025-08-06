package com.managersys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ManagerSysApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerSysApplication.class, args);
    }
}
