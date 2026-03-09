package com.opex.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tax")
@Getter
@Setter
@NoArgsConstructor
public class Tax {

    @Id
    private String id; // Es. "tax_local_..." oppure l'ID del servizio esterno

    private String userId; // L'ID di Keycloak del proprietario

    private LocalDate deadline;
    private String name; // Es. "F24 Acconto IVA", "Bollo Auto"
    private String status; // Es. "PENDING", "PAID"

    private BigDecimal amount;
    private String currency; // Es. "EUR"

    // Flag fondamentale per distinguere se l'abbiamo creata noi o un servizio esterno
    private Boolean isExternal;
}