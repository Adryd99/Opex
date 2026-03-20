package com.opex.backend.controller;

import com.opex.backend.model.invoice.SupplierInvoice;
import com.opex.backend.service.S3Service;
import com.opex.backend.service.SupplierInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/supplier-invoices")
@RequiredArgsConstructor
public class SupplierInvoiceController {
    private final SupplierInvoiceService supplierInvoiceService;
    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<Page<SupplierInvoice>> getMyInvoices(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(supplierInvoiceService.getUserInvoices(userId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierInvoice> getInvoiceById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getClaimAsString("sub");
        return supplierInvoiceService.getInvoiceById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SupplierInvoice> createInvoice(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SupplierInvoice invoice) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(supplierInvoiceService.createInvoice(userId, invoice));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SupplierInvoice> updateInvoice(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody SupplierInvoice invoice) {
        String userId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(supplierInvoiceService.updateInvoice(id, userId, invoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        String userId = jwt.getClaimAsString("sub");
        supplierInvoiceService.deleteInvoice(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadInvoiceFile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file) {
        String userId = jwt.getClaimAsString("sub");
        String fileName = s3Service.uploadFile(file, userId);
        return ResponseEntity.ok(fileName);
    }
}
