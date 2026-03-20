package com.saltedgeproxy.app.saltedgeproxy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SaltEdgeConnectResponse {
    private DataItem data;

    @Data
    public static class DataItem {
        @JsonProperty("expires_at")
        private LocalDateTime expiresAt;
        @JsonProperty("connect_url")
        private String connectUrl;
        @JsonProperty("customer_id")
        private String customerId;
    }
}
