package com.telkom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.telkom"})
public class FormAutofillApplication {
    public static void main(String[] args) {
        SpringApplication.run(FormAutofillApplication.class, args);
    }
}