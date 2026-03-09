package com.opex.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    private String bankAccountId; // Obbligatorio per la POST, opzionale per la PATCH
    private BigDecimal amount;
    private LocalDate bookingDate;
    private String category;
    private String description;
    private String merchantName;
    private String status;
    private String type;
}