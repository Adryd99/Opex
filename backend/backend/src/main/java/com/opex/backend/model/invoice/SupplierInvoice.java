package com.opex.backend.model.invoice;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_invoice")
@Getter
@Setter
@NoArgsConstructor

public class SupplierInvoice {

    @Id
    private Long id;
    private String userId; // L'ID di Keycloak del proprietario
    private String supplierId; // L'ID del supplier
    private String client;
    private String dueDate;
    private String status;
    private String amount;

}
