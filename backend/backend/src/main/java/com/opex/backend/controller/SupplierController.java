package com.opex.backend.controller;

import com.opex.backend.model.invoice.Supplier;
import com.opex.backend.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<Page<Supplier>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable String id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.createSupplier(supplier));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable String id, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
