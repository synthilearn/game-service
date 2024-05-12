package com.synthilearn.gameservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.synthilearn.gameservice.app.config.properties.WebClientProperties;
import com.synthilearn.loggingstarter.EnableLogging;
import com.synthilearn.securestarter.EnableTokenResolver;

@SpringBootApplication
@EnableLogging
@EnableScheduling
@EnableTokenResolver
@EnableConfigurationProperties(WebClientProperties.class)
public class GameServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameServiceApplication.class, args);
    }
}

