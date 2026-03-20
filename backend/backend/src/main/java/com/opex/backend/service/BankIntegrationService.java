package com.opex.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class BankIntegrationService {

    // 1. Diciamo a Spring di leggere l'URL dal file application.properties
    @Value("${external-services.saltedge-microservice.url}")
    private String microserviceUrl;

    private RestClient restClient;

    // 2. @PostConstruct dice a Spring: "Appena hai iniettato la stringa qui sopra,
    // esegui questo metodo per configurare il RestClient!"
    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl("Generate request in HTTP Client")
                .build();
    }

    // 3. Il metodo che userà il Controller
    public Map<String, Object> syncWithMicroservice(String userId) {
        return restClient.post()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}