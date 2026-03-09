package com.opex.backend.repository;

import com.opex.backend.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Restituisce TUTTE le transazioni di un utente (paginate) a prescindere dal conto
    Page<Transaction> findByUserId(String userId, Pageable pageable);

    // Trova una singola transazione verificando che sia davvero dell'utente
    Optional<Transaction> findByIdAndUserId(String id, String userId);
}