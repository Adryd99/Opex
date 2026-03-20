package com.opex.backend.controller;

import com.opex.backend.service.BankIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/bank-integration")
@RequiredArgsConstructor
public class BankIntegrationController {

    private final BankIntegrationService bankIntegrationService;

    /**
     * Endpoint unico per l'integrazione bancaria.
     * Metodo: POST
     * URL: http://localhost:8080/api/bank-integration/sync
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncBank(@AuthenticationPrincipal Jwt jwt) {
        // Estraiamo sempre in modo sicuro l'ID utente dal token
        String userId = jwt.getClaimAsString("sub");

        // Facciamo la chiamata al microservizio
        Map<String, Object> response = bankIntegrationService.syncWithMicroservice(userId);

        // Restituiamo il JSON identico a quello del microservizio
        return ResponseEntity.ok(response);
    }
}