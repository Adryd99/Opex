package com.saltedgeproxy.app.saltedgeproxy.repository;

import com.saltedgeproxy.app.saltedgeproxy.model.BankConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankConnectionRepository extends JpaRepository<BankConnection, String> {
    List<BankConnection> findByUserId(String userId);
}
