package com.hamza.smartleadgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartLeadGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartLeadGeneratorApplication.class, args);
    }

}
