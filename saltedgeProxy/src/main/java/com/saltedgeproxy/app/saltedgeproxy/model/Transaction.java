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

    @Column(name = "user_id")
    private String userId; // Per trovare subito le transazioni dell'utente senza fare JOIN

    @Column(name = "bank_account_id")
    private String bankAccountId; // Collegamento al conto corrente

    private BigDecimal amount;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    private String category;
    private String description;

    @Column(name = "merchant_name")
    private String merchantName;

    private String status;
    private String type;

    // Flag di sicurezza: ci dice subito se questa transazione l'abbiamo creata noi o SaltEdge
    @Column(name = "is_saltedge")
    private Boolean isSaltedge;
}
