package com.opex.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TaxRequest {
    private LocalDate deadline;
    private String name;
    private String status;
    private BigDecimal amount;
    private String currency;
}