package com.saltedgeproxy.app.saltedgeproxy.service;

import com.saltedgeproxy.app.saltedgeproxy.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaltEdgeService {

    @Value("${saltedge.app-id}")
    private String appId;

    @Value("${saltedge.secret}")
    private String secret;

    private final String BASE_URL = "https://www.saltedge.com/api/v6";
    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("App-id", appId);
        headers.set("Secret", secret);
        return headers;
    }

    public SaltEdgeCustomerResponse createCustomer(Map<String, Object> customerData) {
        System.out.println("Creating customer");
        Map<String, Object> body = new HashMap<>();
        body.put("data", customerData);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        return restTemplate.postForObject(BASE_URL + "/customers", entity, SaltEdgeCustomerResponse.class);
    }

    public SaltEdgeConnectResponse connectConnection(String customerId, Map<String, Object> connectParams) {
        System.out.println("Connecting connection");
        SaltEdgeConsentRequest consentRequest = new SaltEdgeConsentRequest();
        List<String> scopes = new ArrayList<>();
        scopes.add("accounts");
        scopes.add("transactions");
        consentRequest.setScopes(scopes);
        Map<String, Object> data = new HashMap<>(connectParams);
        data.put("customer_id", customerId);
        data.put("consent", consentRequest);
        Map<String, Object> body = new HashMap<>();
        body.put("data", data);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
        return restTemplate.postForObject(BASE_URL + "/connections/connect", entity, SaltEdgeConnectResponse.class);
    }

    public SaltEdgeAccountResponse getAccounts(String connectionId) {
        System.out.println("Getting accounts");
        String url = BASE_URL + "/accounts?connection_id=" + connectionId;
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<SaltEdgeAccountResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, SaltEdgeAccountResponse.class);
        return response.getBody();
    }

    public SaltEdgeConnectionsResponse getConnections(String customerId) {
        System.out.println("Getting connections for customer: " + customerId);
        String url = BASE_URL + "/connections?customer_id=" + customerId;
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<SaltEdgeConnectionsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, SaltEdgeConnectionsResponse.class);
        return response.getBody();
    }

    public SaltEdgeTransactionResponse getTransactions(String connectionId, String accountId) {
        System.out.println("Getting transactions");
        String url = BASE_URL + "/transactions?connection_id=" + connectionId + "&account_id=" + accountId;
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        ResponseEntity<SaltEdgeTransactionResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, SaltEdgeTransactionResponse.class);
        return response.getBody();
    }

    public void removeConnection(String connectionId) {
        String url = BASE_URL + "/connections/" + connectionId;
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public void removeCustomer(String customerId) {
        String url = BASE_URL + "/customers/" + customerId;
        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }
}
