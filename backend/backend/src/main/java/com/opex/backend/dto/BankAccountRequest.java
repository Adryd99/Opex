package com.opex.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BankAccountRequest {
    private BigDecimal balance;
    private String institutionName;
    private String country;
    private String currency;
    private Boolean isForTax;
    private String nature;
}