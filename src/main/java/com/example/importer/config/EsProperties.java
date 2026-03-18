package com.example.importer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "es")
public class EsProperties {
    private String host = "127.0.0.1";
    private int port = 9200;
    private String scheme = "http";
    private int connectTimeout = 5000;
    private int socketTimeout = 60000;
    private int bulkActions = 500;
}
