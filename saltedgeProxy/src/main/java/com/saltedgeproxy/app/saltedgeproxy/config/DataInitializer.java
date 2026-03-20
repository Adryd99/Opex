package com.saltedgeproxy.app.saltedgeproxy.config;

import com.saltedgeproxy.app.saltedgeproxy.model.BankAccount;
import com.saltedgeproxy.app.saltedgeproxy.model.BankConnection;
import com.saltedgeproxy.app.saltedgeproxy.model.Transaction;
import com.saltedgeproxy.app.saltedgeproxy.model.User;
import com.saltedgeproxy.app.saltedgeproxy.repository.BankAccountRepository;
import com.saltedgeproxy.app.saltedgeproxy.repository.BankConnectionRepository;
import com.saltedgeproxy.app.saltedgeproxy.repository.TransactionRepository;
import com.saltedgeproxy.app.saltedgeproxy.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                 BankAccountRepository bankAccountRepository,
                                 TransactionRepository transactionRepository,
                                 BankConnectionRepository bankConnectionRepository) {
        return args -> {
            // 1. Creazione Utente 1 (Già connesso a SaltEdge)
            User user1 = new User("user_001", "mario.rossi@example.com", "Mario", "Rossi");
            user1.setCustomerId("saltedge_customer_123");
            user1.setDob(LocalDate.of(1985, 5, 20));
            user1.setIsActive(true);
            userRepository.save(user1);

            // Connessione per Utente 1
            BankConnection conn1 = new BankConnection();
            conn1.setId("saltedge_conn_456");
            conn1.setUserId(user1.getId());
            conn1.setProviderName("Intesa Sanpaolo");
            conn1.setStatus("active");
            conn1.setCreatedAt(LocalDate.now());
            bankConnectionRepository.save(conn1);

            // 2. Creazione Utente 2 (Nuovo, senza SaltEdge)
            User user2 = new User("user_002", "luigi.bianchi@example.com", "Luigi", "Bianchi");
            user2.setCustomerId("1759200260577561427");
            userRepository.save(user2);

            BankConnection conn2 = new BankConnection();
            conn2.setId("1759217008047557386");
            conn2.setUserId(user2.getId());
            conn2.setProviderName("Banca Esempio");
            conn2.setStatus("active");
            conn2.setCreatedAt(LocalDate.now());
            bankConnectionRepository.save(conn2);

            // 3. Creazione Conto Bancario per Utente 1
            BankAccount account1 = new BankAccount();
            account1.setSaltedgeAccountId("account_001");
            account1.setUserId(user1.getId());
            account1.setConnectionId(conn1.getId());
            account1.setBalance(new BigDecimal("1250.50"));
            account1.setInstitutionName("Intesa Sanpaolo");
            account1.setCountry("IT");
            account1.setCurrency("EUR");
            account1.setIsForTax(true);
            account1.setNature("checking");
            account1.setIsSaltedge(true);
            bankAccountRepository.save(account1);

            // 4. Creazione Transazioni per Utente 1
            Transaction t1 = new Transaction();
            t1.setId("trx_001");
            t1.setUserId(user1.getId());
            t1.setBankAccountId(account1.getSaltedgeAccountId());
            t1.setAmount(new BigDecimal("-45.00"));
            t1.setBookingDate(LocalDate.now().minusDays(2));
            t1.setCategory("shopping");
            t1.setDescription("Amazon.it Purchase");
            t1.setMerchantName("Amazon");
            t1.setStatus("COMPLETED");
            t1.setType("DEBIT");
            t1.setIsSaltedge(true);
            transactionRepository.save(t1);

            Transaction t2 = new Transaction();
            t2.setId("trx_002");
            t2.setUserId(user1.getId());
            t2.setBankAccountId(account1.getSaltedgeAccountId());
            t2.setAmount(new BigDecimal("2000.00"));
            t2.setBookingDate(LocalDate.now().minusDays(5));
            t2.setCategory("salary");
            t2.setDescription("Stipendio Marzo");
            t2.setStatus("COMPLETED");
            t2.setType("CREDIT");
            t2.setIsSaltedge(true);
            transactionRepository.save(t2);

            System.out.println("[DEBUG_LOG] Database inizializzato con dati fittizi.");
        };
    }
}
