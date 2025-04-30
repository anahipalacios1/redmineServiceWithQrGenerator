package com.mycompany.qrcode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(prefix = "redmine")
public class RedmineConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${redmine.api.url}")
    private String url;

    @Value("${redmine.issue.api.url}")
    private String urlQr;

    @Value("${redmine.api.key}")
    private String key;

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }

    public String getUrlQr() {
        return urlQr;
    }
}