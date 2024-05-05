package com.synthilearn.gameservice.app.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import lombok.Data;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "web.client")
public class WebClientProperties {

    private String dictionaryHost;
}
