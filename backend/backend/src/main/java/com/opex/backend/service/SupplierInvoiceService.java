package com.opex.backend.service;

import com.opex.backend.model.invoice.SupplierInvoice;
import com.opex.backend.repository.SupplierInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierInvoiceService {

    private final SupplierInvoiceRepository supplierInvoiceRepository;

    public Page<SupplierInvoice> getUserInvoices(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierInvoiceRepository.findByUserId(userId, pageable);
    }

    public Optional<SupplierInvoice> getInvoiceById(Long id, String userId) {
        return supplierInvoiceRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public SupplierInvoice createInvoice(String userId, SupplierInvoice invoice) {
        invoice.setUserId(userId);
        return supplierInvoiceRepository.save(invoice);
    }

    @Transactional
    public SupplierInvoice updateInvoice(Long id, String userId, SupplierInvoice invoiceDetails) {
        SupplierInvoice invoice = supplierInvoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Fattura fornitore non trovata o non autorizzata"));

        if (invoiceDetails.getSupplierId() != null) invoice.setSupplierId(invoiceDetails.getSupplierId());
        if (invoiceDetails.getClient() != null) invoice.setClient(invoiceDetails.getClient());
        if (invoiceDetails.getDueDate() != null) invoice.setDueDate(invoiceDetails.getDueDate());
        if (invoiceDetails.getStatus() != null) invoice.setStatus(invoiceDetails.getStatus());
        if (invoiceDetails.getAmount() != null) invoice.setAmount(invoiceDetails.getAmount());

        return supplierInvoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Long id, String userId) {
        SupplierInvoice invoice = supplierInvoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Fattura fornitore non trovata o non autorizzata"));
        supplierInvoiceRepository.delete(invoice);
    }
}
