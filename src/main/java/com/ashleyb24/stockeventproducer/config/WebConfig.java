package com.ashleyb24.stockeventproducer.config;

import com.ashleyb24.jalphavantage.AlphaVantage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AlphaVantage alphaVantage() {
        return new AlphaVantage(apiKey, restTemplate());
    }
}
