package com.opex.backend.controller;

import com.opex.backend.dto.TransactionRequest;
import com.opex.backend.model.Transaction;
import com.opex.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 1. Ritorna la lista paginata di TUTTE le transazioni dell'utente (SaltEdge + Locali)
    @GetMapping("/my-transactions")
    public ResponseEntity<Page<Transaction>> getMyTransactions(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwt.getClaimAsString("sub");
        Page<Transaction> transactions = transactionService.getUserTransactions(userId, page, size);
        return ResponseEntity.ok(transactions);
    }

    // 2. Crea una transazione manuale su un conto locale
    @PostMapping("/local")
    public ResponseEntity<Transaction> createLocalTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody TransactionRequest request) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(transactionService.createLocalTransaction(userId, request));
    }

    // 3. Modifica una transazione manuale
    @PatchMapping("/local/{transactionId}")
    public ResponseEntity<Transaction> updateLocalTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String transactionId,
            @RequestBody TransactionRequest request) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(transactionService.updateLocalTransaction(userId, transactionId, request));
    }

    // 4. Elimina una transazione manuale
    @DeleteMapping("/local/{transactionId}")
    public ResponseEntity<Void> deleteLocalTransaction(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String transactionId) {
        String userId = jwt.getClaimAsString("sub");

        transactionService.deleteLocalTransaction(userId, transactionId);

        // Ritorniamo 204 No Content, lo standard REST per le eliminazioni riuscite
        return ResponseEntity.noContent().build();
    }
}