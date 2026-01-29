package com.healthtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiHealthTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiHealthTrackerApplication.class, args);
    }
}
