package com.opex.backend.service;

import com.opex.backend.dto.BankAccountRequest;
import com.opex.backend.model.BankAccount;
import com.opex.backend.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    // --- LEGGE TUTTI I CONTI (PAGINATO) ---
    public Page<BankAccount> getUserAccounts(String userId, int page, int size) {
        // Crea l'oggetto di paginazione (page parte da 0)
        Pageable pageable = PageRequest.of(page, size);
        return bankAccountRepository.findByUserId(userId, pageable);
    }

    // --- CREA UN CONTO LOCALE ---
    @Transactional
    public BankAccount createLocalAccount(String userId, BankAccountRequest request) {
        BankAccount account = new BankAccount();
        account.setSaltedgeAccountId("local_" + UUID.randomUUID().toString());
        account.setUserId(userId);
        account.setIsSaltedge(false);
        account.setConnectionId(null);

        account.setBalance(request.getBalance());
        account.setInstitutionName(request.getInstitutionName());
        account.setCountry(request.getCountry());
        account.setCurrency(request.getCurrency());
        account.setIsForTax(request.getIsForTax());
        account.setNature(request.getNature());

        return bankAccountRepository.save(account);
    }

    // --- AGGIORNA UN CONTO LOCALE ---
    @Transactional
    public BankAccount updateLocalAccount(String userId, String accountId, BankAccountRequest request) {
        BankAccount account = bankAccountRepository.findBySaltedgeAccountIdAndUserId(accountId, userId)
                .orElseThrow(() -> new RuntimeException("Conto non trovato o non autorizzato"));

        if (Boolean.TRUE.equals(account.getIsSaltedge())) {
            throw new RuntimeException("Operazione negata. Non puoi modificare manualmente i conti di SaltEdge.");
        }

        if (request.getBalance() != null) account.setBalance(request.getBalance());
        if (request.getInstitutionName() != null) account.setInstitutionName(request.getInstitutionName());
        if (request.getCountry() != null) account.setCountry(request.getCountry());
        if (request.getCurrency() != null) account.setCurrency(request.getCurrency());
        if (request.getIsForTax() != null) account.setIsForTax(request.getIsForTax());
        if (request.getNature() != null) account.setNature(request.getNature());

        return bankAccountRepository.save(account);
    }
}