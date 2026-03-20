package com.saltedgeproxy.app.saltedgeproxy.controller;

import com.saltedgeproxy.app.saltedgeproxy.dto.*;
import com.saltedgeproxy.app.saltedgeproxy.model.BankAccount;
import com.saltedgeproxy.app.saltedgeproxy.model.Transaction;
import com.saltedgeproxy.app.saltedgeproxy.model.User;
import com.saltedgeproxy.app.saltedgeproxy.repository.BankAccountRepository;
import com.saltedgeproxy.app.saltedgeproxy.repository.TransactionRepository;
import com.saltedgeproxy.app.saltedgeproxy.repository.UserRepository;
import com.saltedgeproxy.app.saltedgeproxy.service.SaltEdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SaltEdgeService saltEdgeService;

    @PostMapping("/{id}")
    public ResponseEntity<String> createUser(@PathVariable String id) {
        System.out.println("Creating user: " + id);
        // Recupero l'utente dal database (precedentemente inserito via Keycloak o altro processo)
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        // 1. Creazione Customer su SaltEdge (se non già presente)
        if (user.getCustomerId() == null && !user.getIsActiveSaltedge()) {
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("email", user.getEmail() != null ? user.getEmail() : user.getId());
            SaltEdgeCustomerResponse customerResponse = saltEdgeService.createCustomer(customerData);
            if (customerResponse != null && customerResponse.getData() != null) {
                user.setCustomerId(customerResponse.getData().getCustomerId());
                SaltEdgeConnectResponse connectResponse = saltEdgeService.connectConnection(user.getCustomerId(), customerData);
                if (connectResponse != null && connectResponse.getData() != null) {
                    String connectionUrl = connectResponse.getData().getConnectUrl();
                    userRepository.save(user);
                    return ResponseEntity.ok(connectionUrl);
                }
            }
        }

        user.setIsActiveSaltedge(true);
        User savedUser = userRepository.save(user);

        // Se abbiamo una connessione (già salvata a DB), sincronizziamo subito Account e Transazioni
        if (savedUser.getConnectionId() != null) {
            syncUserData(savedUser);
        }

        return ResponseEntity.ok(savedUser.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        return userRepository.findById(id).map(user -> {
            user.setIsActiveSaltedge(false);
            userRepository.save(user);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<String> syncUser(@PathVariable String id) {
        return userRepository.findById(id).map(user -> {
            if (user.getConnectionId() != null) {
                syncUserData(user);
            }
            return ResponseEntity.ok(user.getId());
        }).orElse(ResponseEntity.notFound().build());
    }

    private void syncUserData(User user) {
        // 1. Get Accounts
        SaltEdgeAccountResponse accountsResponse = saltEdgeService.getAccounts(user.getConnectionId());
        if (accountsResponse != null && accountsResponse.getData() != null) {
            for (SaltEdgeAccountResponse.AccountItem item : accountsResponse.getData()) {
                BankAccount account = bankAccountRepository.findById(item.getId()).orElse(new BankAccount());
                account.setSaltedgeAccountId(item.getId());
                account.setUserId(user.getId());
                account.setConnectionId(item.getConnectionId());
                account.setBalance(item.getBalance());
                account.setInstitutionName(item.getName()); // Saltedge non dà sempre institution_name qui, ma usiamo name
                account.setCurrency(item.getCurrencyCode());
                account.setNature(item.getNature());
                account.setIsSaltedge(true);
                if (account.getIsForTax() == null) account.setIsForTax(false);

                bankAccountRepository.save(account);

                // 2. Get Transactions for this account
                SaltEdgeTransactionResponse transactionsResponse = saltEdgeService.getTransactions(user.getConnectionId(), item.getId());
                if (transactionsResponse != null && transactionsResponse.getData() != null) {
                    for (SaltEdgeTransactionResponse.TransactionItem tItem : transactionsResponse.getData()) {
                        Transaction transaction = transactionRepository.findById(tItem.getId()).orElse(new Transaction());
                        transaction.setId(tItem.getId());
                        transaction.setUserId(user.getId());
                        transaction.setBankAccountId(account.getSaltedgeAccountId());
                        transaction.setAmount(tItem.getAmount());
                        transaction.setBookingDate(tItem.getMadeOn()); // Mapping Saltedge made_on -> booking_date
                        transaction.setCategory(tItem.getCategory());
                        transaction.setDescription(tItem.getDescription());
                        transaction.setStatus(tItem.getStatus());
                        transaction.setType(tItem.getAmount().compareTo(BigDecimal.ZERO) >= 0 ? "CREDIT" : "DEBIT");
                        transaction.setIsSaltedge(true);

                        if (tItem.getExtra() != null && tItem.getExtra().containsKey("merchant_name")) {
                            transaction.setMerchantName((String) tItem.getExtra().get("merchant_name"));
                        }

                        transactionRepository.save(transaction);
                    }
                }
            }
        }
    }
}
