package com.stocktrading.dataloader1;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@SpringBootApplication
@AllArgsConstructor
@EnableScheduling
@EnableAsync
public class DataLoader1Application {

    static void main(String[] args) {
        SpringApplication.run(DataLoader1Application.class, args);
    }
}