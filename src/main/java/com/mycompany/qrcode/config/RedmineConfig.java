package com.mycompany.qrcode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RedmineConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${redmine.api.url}")
    private String url;

    @Value("${redmine.api.key}")
    private String key;

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }
}