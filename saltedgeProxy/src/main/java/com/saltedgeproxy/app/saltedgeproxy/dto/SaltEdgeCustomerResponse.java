package com.saltedgeproxy.app.saltedgeproxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SaltEdgeCustomerResponse {
    private DataItem data;

    @Data
    public static class DataItem {
        private String id;
        private String email;
        @JsonProperty("customer_id")
        private String customerId;
        private String identifier;
        @JsonProperty("blocked_at")
        private LocalDateTime blockedAt;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
    }
}
