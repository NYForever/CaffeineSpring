package com.ny.caffeinespring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CaffeineSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaffeineSpringApplication.class, args);
    }

}
