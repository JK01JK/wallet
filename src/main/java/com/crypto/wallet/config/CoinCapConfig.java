package com.crypto.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CoinCapConfig {

    @Bean
    public WebClient coinCapWebClient(
            @Value("${coincap.base-url}") String baseUrl,
            @Value("${coincap.api.key}") String apiKey
    ) {
        // Add Bearer token to every request as required by CoinCap v3
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }
}
