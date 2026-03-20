package com.opex.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
public class BankAccount {

    @Id
    @Column(name = "saltedge_account_id")
    private String saltedgeAccountId; // Usato per l'ID di SaltEdge, oppure conterrà un UUID per i conti locali

    @Column(name = "user_id")
    private String userId; // L'ID di Keycloak del proprietario

    @Column(name = "connection_id")
    private String connectionId; // Null per i conti locali, valorizzato per quelli di SaltEdge

    private BigDecimal balance;

    @Column(name = "institution_name")
    private String institutionName;

    private String country;
    private String currency;

    @Column(name = "is_for_tax")
    private Boolean isForTax;

    private String nature;

    // Flag fondamentale per distinguere la provenienza
    @Column(name = "is_saltedge")
    private Boolean isSaltedge;
}