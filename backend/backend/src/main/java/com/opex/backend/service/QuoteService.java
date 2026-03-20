package com.opex.backend.service;

import com.opex.backend.model.invoice.Quote;
import com.opex.backend.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;

    public Page<Quote> getUserQuotes(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quoteRepository.findByUserId(userId, pageable);
    }

    public Optional<Quote> getQuoteById(Long id, String userId) {
        return quoteRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public Quote createQuote(String userId, Quote quote) {
        quote.setUserId(userId);
        return quoteRepository.save(quote);
    }

    @Transactional
    public Quote updateQuote(Long id, String userId, Quote quoteDetails) {
        Quote quote = quoteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Preventivo non trovato o non autorizzato"));

        if (quoteDetails.getQuote() != null) quote.setQuote(quoteDetails.getQuote());
        if (quoteDetails.getClient() != null) quote.setClient(quoteDetails.getClient());
        if (quoteDetails.getDate() != null) quote.setDate(quoteDetails.getDate());
        if (quoteDetails.getExpiryDate() != null) quote.setExpiryDate(quoteDetails.getExpiryDate());
        if (quoteDetails.getStatus() != null) quote.setStatus(quoteDetails.getStatus());
        if (quoteDetails.getAmount() != null) quote.setAmount(quoteDetails.getAmount());

        return quoteRepository.save(quote);
    }

    @Transactional
    public void deleteQuote(Long id, String userId) {
        Quote quote = quoteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Preventivo non trovato o non autorizzato"));
        quoteRepository.delete(quote);
    }
}
