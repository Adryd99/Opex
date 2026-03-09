package com.opex.backend.service;

import com.opex.backend.dto.TransactionRequest;
import com.opex.backend.model.BankAccount;
import com.opex.backend.model.Transaction;
import com.opex.backend.repository.BankAccountRepository;
import com.opex.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository; // Ci serve per validare il conto!

    // --- 1. LEGGE TUTTE LE TRANSAZIONI (PAGINATO) ---
    public Page<Transaction> getUserTransactions(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByUserId(userId, pageable);
    }

    // --- 2. CREA UNA TRANSAZIONE LOCALE ---
    @Transactional
    public Transaction createLocalTransaction(String userId, TransactionRequest request) {
        // Controllo di sicurezza: Il conto bancario esiste? È di questo utente?
        BankAccount account = bankAccountRepository.findBySaltedgeAccountIdAndUserId(request.getBankAccountId(), userId)
                .orElseThrow(() -> new RuntimeException("Conto bancario non trovato o non autorizzato"));

        // Controllo fondamentale: È un conto locale?
        if (Boolean.TRUE.equals(account.getIsSaltedge())) {
            throw new RuntimeException("Impossibile creare transazioni manuali su un conto SaltEdge.");
        }

        Transaction transaction = new Transaction();
        transaction.setId("trx_local_" + UUID.randomUUID().toString());
        transaction.setUserId(userId);
        transaction.setBankAccountId(request.getBankAccountId());
        transaction.setIsSaltedge(false); // È una nostra transazione manuale

        transaction.setAmount(request.getAmount());
        transaction.setBookingDate(request.getBookingDate());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());
        transaction.setMerchantName(request.getMerchantName());
        transaction.setStatus(request.getStatus());
        transaction.setType(request.getType());

        return transactionRepository.save(transaction);
    }

    // --- 3. AGGIORNA UNA TRANSAZIONE LOCALE ---
    @Transactional
    public Transaction updateLocalTransaction(String userId, String transactionId, TransactionRequest request) {
        // Cerco la transazione e mi assicuro che sia dell'utente
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transazione non trovata o non autorizzata"));

        // Non posso modificare le transazioni vere arrivate da SaltEdge!
        if (Boolean.TRUE.equals(transaction.getIsSaltedge())) {
            throw new RuntimeException("Impossibile modificare transazioni importate da SaltEdge.");
        }

        // Se l'utente vuole spostare la transazione su un altro conto, valido anche il nuovo conto
        if (request.getBankAccountId() != null) {
            BankAccount newAccount = bankAccountRepository.findBySaltedgeAccountIdAndUserId(request.getBankAccountId(), userId)
                    .orElseThrow(() -> new RuntimeException("Nuovo conto bancario non valido"));
            if (Boolean.TRUE.equals(newAccount.getIsSaltedge())) {
                throw new RuntimeException("Non puoi spostare la transazione su un conto SaltEdge.");
            }
            transaction.setBankAccountId(request.getBankAccountId());
        }

        // Logica PATCH: aggiorno solo i campi presenti
        if (request.getAmount() != null) transaction.setAmount(request.getAmount());
        if (request.getBookingDate() != null) transaction.setBookingDate(request.getBookingDate());
        if (request.getCategory() != null) transaction.setCategory(request.getCategory());
        if (request.getDescription() != null) transaction.setDescription(request.getDescription());
        if (request.getMerchantName() != null) transaction.setMerchantName(request.getMerchantName());
        if (request.getStatus() != null) transaction.setStatus(request.getStatus());
        if (request.getType() != null) transaction.setType(request.getType());

        return transactionRepository.save(transaction);
    }

    // --- 4. CANCELLA UNA TRANSAZIONE LOCALE ---
    @Transactional
    public void deleteLocalTransaction(String userId, String transactionId) {
        // Cerco la transazione e mi assicuro che sia davvero dell'utente
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transazione non trovata o non autorizzata"));

        // Protezione fondamentale: Non posso cancellare le transazioni vere arrivate da SaltEdge!
        if (Boolean.TRUE.equals(transaction.getIsSaltedge())) {
            throw new RuntimeException("Impossibile cancellare transazioni importate da SaltEdge.");
        }

        // Se è locale, procedo con l'eliminazione fisica dal database
        transactionRepository.delete(transaction);
    }
}