package com.opex.backend.model.invoice;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quote")
@Getter
@Setter
@NoArgsConstructor
public class Quote {

    @Id
    private Long id;
    private String userId; // L'ID di Keycloak del proprietario
    private String quote;
    private String client;
    private String date;
    private String expiryDate;
    private String status;
    private String amount;

}
