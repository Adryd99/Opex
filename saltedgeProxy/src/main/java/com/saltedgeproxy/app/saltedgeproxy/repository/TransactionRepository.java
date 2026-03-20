package com.saltedgeproxy.app.saltedgeproxy.repository;

import com.saltedgeproxy.app.saltedgeproxy.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByBankAccountId(String bankAccountId);
    List<Transaction> findByUserId(String userId);
}
