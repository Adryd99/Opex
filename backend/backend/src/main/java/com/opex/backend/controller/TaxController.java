package com.opex.backend.controller;

import com.opex.backend.dto.TaxRequest;
import com.opex.backend.model.Tax;
import com.opex.backend.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
public class TaxController {

    private final TaxService taxService;

    // 1. Ritorna la lista paginata di TUTTE le tasse (Esterne + Locali)
    @GetMapping("/my-taxes")
    public ResponseEntity<Page<Tax>> getMyTaxes(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwt.getClaimAsString("sub");
        Page<Tax> taxes = taxService.getUserTaxes(userId, page, size);
        return ResponseEntity.ok(taxes);
    }

    // 2. Crea una tassa manuale
    @PostMapping("/local")
    public ResponseEntity<Tax> createLocalTax(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody TaxRequest request) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(taxService.createLocalTax(userId, request));
    }

    // 3. Modifica una tassa manuale
    @PatchMapping("/local/{taxId}")
    public ResponseEntity<Tax> updateLocalTax(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String taxId,
            @RequestBody TaxRequest request) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(taxService.updateLocalTax(userId, taxId, request));
    }

    // 4. Elimina una tassa manuale
    @DeleteMapping("/local/{taxId}")
    public ResponseEntity<Void> deleteLocalTax(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String taxId) {
        String userId = jwt.getClaimAsString("sub");
        taxService.deleteLocalTax(userId, taxId);
        // Ritorniamo 204 No Content, che è lo standard REST quando si elimina qualcosa con successo
        return ResponseEntity.noContent().build();
    }
}