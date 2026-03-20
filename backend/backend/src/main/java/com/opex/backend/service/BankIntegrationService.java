package com.opex.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class BankIntegrationService {

    private final RestClient restClient;

    public BankIntegrationService(@Value("${external-services.saltedge-microservice.url}") String microserviceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(microserviceUrl)
                .build();
    }

    public Map<String, Object> syncWithMicroservice(String userId) {
        return restClient.post()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}