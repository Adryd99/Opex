package com.saltedgeproxy.app.saltedgeproxy.repository;

import com.saltedgeproxy.app.saltedgeproxy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByConnectionId(String connectionId);
    List<BankAccount> findByUserId(String userId);
}
