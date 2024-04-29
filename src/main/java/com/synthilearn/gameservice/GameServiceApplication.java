package com.synthilearn.gameservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.synthilearn.loggingstarter.EnableLogging;
import com.synthilearn.securestarter.EnableTokenResolver;

@SpringBootApplication
@EnableLogging
@EnableTokenResolver
public class GameServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameServiceApplication.class, args);
    }
}

