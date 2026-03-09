package com.opex.backend.service;

import com.opex.backend.dto.TaxRequest;
import com.opex.backend.model.Tax;
import com.opex.backend.repository.TaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaxService {

    private final TaxRepository taxRepository;

    // --- 1. LEGGE TUTTE LE TASSE (PAGINATO) ---
    public Page<Tax> getUserTaxes(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taxRepository.findByUserId(userId, pageable);
    }

    // --- 2. CREA UNA TASSA LOCALE (MANUALE) ---
    @Transactional
    public Tax createLocalTax(String userId, TaxRequest request) {
        Tax tax = new Tax();
        // Generiamo un ID fittizio per le tasse manuali
        tax.setId("tax_local_" + UUID.randomUUID().toString());
        tax.setUserId(userId);

        // La stiamo creando a mano, quindi non è esterna!
        tax.setIsExternal(false);

        tax.setDeadline(request.getDeadline());
        tax.setName(request.getName());
        tax.setStatus(request.getStatus());
        tax.setAmount(request.getAmount());
        tax.setCurrency(request.getCurrency());

        return taxRepository.save(tax);
    }

    // --- 3. AGGIORNA UNA TASSA LOCALE ---
    @Transactional
    public Tax updateLocalTax(String userId, String taxId, TaxRequest request) {
        // Cerco la tassa e mi assicuro che appartenga all'utente
        Tax tax = taxRepository.findByIdAndUserId(taxId, userId)
                .orElseThrow(() -> new RuntimeException("Tassa non trovata o non autorizzata"));

        // Protezione contro le modifiche illecite!
        if (Boolean.TRUE.equals(tax.getIsExternal())) {
            throw new RuntimeException("Impossibile modificare una scadenza fiscale generata da un servizio esterno.");
        }

        // Logica PATCH: Aggiorno solo i campi inviati
        if (request.getDeadline() != null) tax.setDeadline(request.getDeadline());
        if (request.getName() != null) tax.setName(request.getName());
        if (request.getStatus() != null) tax.setStatus(request.getStatus());
        if (request.getAmount() != null) tax.setAmount(request.getAmount());
        if (request.getCurrency() != null) tax.setCurrency(request.getCurrency());

        return taxRepository.save(tax);
    }

    // --- 4. CANCELLA UNA TASSA LOCALE ---
    @Transactional
    public void deleteLocalTax(String userId, String taxId) {
        // Cerco la tassa e mi assicuro che appartenga all'utente
        Tax tax = taxRepository.findByIdAndUserId(taxId, userId)
                .orElseThrow(() -> new RuntimeException("Tassa non trovata o non autorizzata"));

        // Protezione contro le cancellazioni illecite!
        if (Boolean.TRUE.equals(tax.getIsExternal())) {
            throw new RuntimeException("Impossibile cancellare una scadenza fiscale generata da un servizio esterno.");
        }

        // Procedo con l'eliminazione
        taxRepository.delete(tax);
    }
}