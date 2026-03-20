package com.opex.backend.controller;

import com.opex.backend.model.invoice.Quote;
import com.opex.backend.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping
    public ResponseEntity<Page<Quote>> getMyQuotes(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(quoteService.getUserQuotes(userId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quote> getQuoteById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getClaimAsString("sub");
        return quoteService.getQuoteById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Quote> createQuote(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Quote quote) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(quoteService.createQuote(userId, quote));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Quote> updateQuote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody Quote quote) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(quoteService.updateQuote(id, userId, quote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getClaimAsString("sub");
        quoteService.deleteQuote(id, userId);
        return ResponseEntity.noContent().build();
    }
}
