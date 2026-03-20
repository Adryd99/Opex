package com.opex.backend.repository;

import com.opex.backend.model.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    // Trova tutti i conti dell'utente
    Page<BankAccount> findByUserId(String userId, Pageable pageable);

    // Trova un conto specifico controllando anche il proprietario (previene accessi illeciti)
    Optional<BankAccount> findBySaltedgeAccountIdAndUserId(String saltedgeAccountId, String userId);
}