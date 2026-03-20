package com.opex.backend.repository;

import com.opex.backend.model.invoice.SupplierInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoice, Long> {
    Page<SupplierInvoice> findByUserId(String userId, Pageable pageable);
    Optional<SupplierInvoice> findByIdAndUserId(Long id, String userId);
}
