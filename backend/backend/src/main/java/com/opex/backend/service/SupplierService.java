package com.opex.backend.service;

import com.opex.backend.model.invoice.Supplier;
import com.opex.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Page<Supplier> getAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierRepository.findAll(pageable);
    }

    public Optional<Supplier> getSupplierById(String supplierId) {
        return supplierRepository.findById(supplierId);
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(String supplierId, Supplier supplierDetails) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Fornitore non trovato"));

        if (supplierDetails.getName() != null) supplier.setName(supplierDetails.getName());
        if (supplierDetails.getContact() != null) supplier.setContact(supplierDetails.getContact());
        if (supplierDetails.getEmail() != null) supplier.setEmail(supplierDetails.getEmail());
        if (supplierDetails.getPhone() != null) supplier.setPhone(supplierDetails.getPhone());
        if (supplierDetails.getTaxId() != null) supplier.setTaxId(supplierDetails.getTaxId());
        if (supplierDetails.getStatus() != null) supplier.setStatus(supplierDetails.getStatus());
        if (supplierDetails.getSpend() != null) supplier.setSpend(supplierDetails.getSpend());

        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(String supplierId) {
        supplierRepository.deleteById(supplierId);
    }
}
