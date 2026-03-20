package com.saltedgeproxy.app.saltedgeproxy.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    private String id; // L'ID della transazione (UUID per le nostre, ID esterno per SaltEdge)

    private String userId; // Per trovare subito le transazioni dell'utente senza fare JOIN

    private String bankAccountId; // Collegamento al conto corrente

    private BigDecimal amount;
    private LocalDate bookingDate;

    private String category;
    private String description;
    private String merchantName;
    private String status;
    private String type;

    // Flag di sicurezza: ci dice subito se questa transazione l'abbiamo creata noi o SaltEdge
    private Boolean isSaltedge;
}
