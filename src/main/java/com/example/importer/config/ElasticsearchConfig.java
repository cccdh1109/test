package com.example.importer.config;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final EsProperties esProperties;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
            RestClient.builder(new HttpHost(esProperties.getHost(), esProperties.getPort(), esProperties.getScheme()))
                .setRequestConfigCallback(builder -> builder
                    .setConnectTimeout(esProperties.getConnectTimeout())
                    .setSocketTimeout(esProperties.getSocketTimeout())));
    }

    @Bean
    public RequestOptions requestOptions() {
        return RequestOptions.DEFAULT;
    }
}
