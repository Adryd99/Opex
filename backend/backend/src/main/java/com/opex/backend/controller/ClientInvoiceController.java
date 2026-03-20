package com.opex.backend.controller;

import com.opex.backend.model.invoice.ClientInvoice;
import com.opex.backend.service.ClientInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client-invoices")
@RequiredArgsConstructor
public class ClientInvoiceController {

    private final ClientInvoiceService clientInvoiceService;

    @GetMapping
    public ResponseEntity<Page<ClientInvoice>> getMyInvoices(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(clientInvoiceService.getUserInvoices(userId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientInvoice> getInvoiceById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getClaimAsString("sub");
        return clientInvoiceService.getInvoiceById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClientInvoice> createInvoice(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ClientInvoice invoice) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(clientInvoiceService.createInvoice(userId, invoice));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientInvoice> updateInvoice(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody ClientInvoice invoice) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(clientInvoiceService.updateInvoice(id, userId, invoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getClaimAsString("sub");
        clientInvoiceService.deleteInvoice(id, userId);
        return ResponseEntity.noContent().build();
    }
}
