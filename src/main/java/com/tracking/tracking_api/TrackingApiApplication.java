package com.tracking.tracking_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TrackingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackingApiApplication.class, args);
    }

} 