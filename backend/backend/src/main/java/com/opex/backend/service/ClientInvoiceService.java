package com.opex.backend.service;

import com.opex.backend.model.invoice.ClientInvoice;
import com.opex.backend.repository.ClientInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientInvoiceService {

    private final ClientInvoiceRepository clientInvoiceRepository;

    public Page<ClientInvoice> getUserInvoices(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return clientInvoiceRepository.findByUserId(userId, pageable);
    }

    public Optional<ClientInvoice> getInvoiceById(Long id, String userId) {
        return clientInvoiceRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    public ClientInvoice createInvoice(String userId, ClientInvoice invoice) {
        invoice.setUserId(userId);
        return clientInvoiceRepository.save(invoice);
    }

    @Transactional
    public ClientInvoice updateInvoice(Long id, String userId, ClientInvoice invoiceDetails) {
        ClientInvoice invoice = clientInvoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Fattura non trovata o non autorizzata"));

        if (invoiceDetails.getInvoice() != null) invoice.setInvoice(invoiceDetails.getInvoice());
        if (invoiceDetails.getClient() != null) invoice.setClient(invoiceDetails.getClient());
        if (invoiceDetails.getDate() != null) invoice.setDate(invoiceDetails.getDate());
        if (invoiceDetails.getDueDate() != null) invoice.setDueDate(invoiceDetails.getDueDate());
        if (invoiceDetails.getStatus() != null) invoice.setStatus(invoiceDetails.getStatus());
        if (invoiceDetails.getAmount() != null) invoice.setAmount(invoiceDetails.getAmount());

        return clientInvoiceRepository.save(invoice);
    }

    @Transactional
    public void deleteInvoice(Long id, String userId) {
        ClientInvoice invoice = clientInvoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Fattura non trovata o non autorizzata"));
        clientInvoiceRepository.delete(invoice);
    }
}
