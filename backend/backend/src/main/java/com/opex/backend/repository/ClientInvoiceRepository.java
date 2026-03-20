package com.opex.backend.repository;

import com.opex.backend.model.invoice.ClientInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientInvoiceRepository extends JpaRepository<ClientInvoice, Long> {
    Page<ClientInvoice> findByUserId(String userId, Pageable pageable);
    Optional<ClientInvoice> findByIdAndUserId(Long id, String userId);
}
