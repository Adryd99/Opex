package com.opex.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "bank_connection")
@Getter
@Setter
@NoArgsConstructor
public class BankConnection {

    @Id
    private String id; // Chiave Primaria (PK). È il vero connection_id di SaltEdge.

    @Column(name = "user_id")
    private String userId; // Chiave Esterna (FK). Collega la connessione all'utente in users.

    private String providerName; // Nome della banca (es. "Intesa Sanpaolo"). Utile per la UI!

    private String status; // Stato (es. "active", "inactive"). Fondamentale perché in Europa le connessioni bancarie scadono ogni 90/180 giorni!

    private LocalDate createdAt; // Data in cui l'utente ha collegato questa banca.
}
