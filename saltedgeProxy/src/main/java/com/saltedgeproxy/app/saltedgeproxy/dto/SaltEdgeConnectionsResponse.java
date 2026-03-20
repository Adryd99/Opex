package com.saltedgeproxy.app.saltedgeproxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaltEdgeConnectionsResponse {
    private List<ConnectionItem> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectionItem {
        private String id;
        @JsonProperty("customer_id")
        private String customerId;
        @JsonProperty("provider_code")
        private String providerCode;
        @JsonProperty("provider_name")
        private String providerName;
        private String status;
    }
}
