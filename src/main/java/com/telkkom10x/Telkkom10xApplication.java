package com.telkkom10x;

import com.telkkom10x.database.DatabaseIntializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Telkkom10xApplication {
    public static void main(String[] args) {
        DatabaseIntializer.initialize();
        SpringApplication.run(Telkkom10xApplication.class, args);
    }
}