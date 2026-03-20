package com.saltedgeproxy.app.saltedgeproxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class SaltEdgeAccountResponse {
    private List<AccountItem> data;

    @Data
    public static class AccountItem {
        private String id;
        @JsonProperty("connection_id")
        private String connectionId;
        private String name;
        private String nature;
        private BigDecimal balance;
        @JsonProperty("currency_code")
        private String currencyCode;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        private Map<String, Object> extra;
    }
}
