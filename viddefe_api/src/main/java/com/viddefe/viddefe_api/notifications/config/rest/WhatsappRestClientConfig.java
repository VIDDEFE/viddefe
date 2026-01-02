package com.viddefe.viddefe_api.notifications.config.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class WhatsappRestClientConfig {

    @Bean
    public RestClient whatsappRestClient(
            @Value("${whatsapp.api.token}") String token
    ) {
        return RestClient.builder()
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + token
                )
                .build();
    }
}