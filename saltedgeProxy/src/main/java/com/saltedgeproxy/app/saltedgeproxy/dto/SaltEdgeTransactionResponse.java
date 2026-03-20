package com.saltedgeproxy.app.saltedgeproxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SaltEdgeTransactionResponse {
    private List<TransactionItem> data;

    @Data
    public static class TransactionItem {
        private String id;
        @JsonProperty("account_id")
        private String accountId;
        private Boolean duplicated;
        private String status;
        @JsonProperty("made_on")
        private LocalDate madeOn;
        private BigDecimal amount;
        @JsonProperty("currency_code")
        private String currencyCode;
        private String description;
        private String category;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        private Map<String, Object> extra;
    }
}
