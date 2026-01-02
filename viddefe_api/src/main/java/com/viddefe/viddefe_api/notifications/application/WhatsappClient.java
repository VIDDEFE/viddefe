package com.viddefe.viddefe_api.notifications.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WhatsappClient {
    private final RestClient restClient;

    @Value("whatsapp.api.url")
    private String GRAPH_BASE_URL ;

    @Value("whatsapp.api.phone.number.id")
    private String PHONE_NUMBER_ID;

    public void sendTextMessage(
            String to,
            String message
    ) {

        String url = GRAPH_BASE_URL + "/" + PHONE_NUMBER_ID + "/messages";

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of(
                        "body", message
                )
        );

        restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

}
