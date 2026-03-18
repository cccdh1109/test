package com.example.importer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.example.importer.mapper")
@SpringBootApplication
public class ImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImporterApplication.class, args);
    }
}
