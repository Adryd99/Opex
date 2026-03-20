package com.saltedgeproxy.app.saltedgeproxy.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
public class BankAccount {

    @Id
    private String saltedgeAccountId; // Usato per l'ID di SaltEdge, oppure conterrà un UUID per i conti locali

    private String userId; // L'ID di Keycloak del proprietario

    private String connectionId; // Null per i conti locali, valorizzato per quelli di SaltEdge

    private BigDecimal balance;
    private String institutionName;
    private String country;
    private String currency;
    private Boolean isForTax;
    private String nature;

    // Flag fondamentale per distinguere la provenienza
    private Boolean isSaltedge;
}
