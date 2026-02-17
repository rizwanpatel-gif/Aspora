package com.aspora.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${openmeteo.base-url}")
    private String openMeteoBaseUrl;

    @Bean
    public RestClient openMeteoRestClient() {
        return RestClient.builder()
                .baseUrl(openMeteoBaseUrl)
                .build();
    }
}
