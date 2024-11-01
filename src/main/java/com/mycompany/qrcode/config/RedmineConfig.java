package com.mycompany.qrcode.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedmineConfig {

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