# SaltEdge Proxy App

Questa applicazione è un middleware progettato per gestire l'integrazione con **SaltEdge (v6)**. Espone un set ridotto di API per la gestione degli utenti, sincronizzando conti e transazioni bancarie nel database locale.

## 🚀 API Esposte

L'applicazione espone solo 3 endpoint principali:

### 1. Creazione Utente + Flusso SaltEdge
Recupera l'utente dal database locale (precedentemente censito) e genera un `customer_id` su SaltEdge se non presente. Se l'utente ha già un `connection_id`, sincronizza immediatamente conti e transazioni.

**Endpoint:** `POST /api/users/{id}`

**cURL:**
```bash
curl --location --request POST 'http://localhost:8080/api/users/user_001'
```

### 2. Eliminazione Utente (Soft Delete)
Imposta il flag `is_active` a `false` per l'utente specificato.

**Endpoint:** `DELETE /api/users/{id}`

**cURL:**
```bash
curl --location --request DELETE 'http://localhost:8080/api/users/user_001'
```

### 3. Sincronizzazione Dati
Forza la sincronizzazione di conti (`BankAccount`) e transazioni (`Transaction`) da SaltEdge per l'utente specificato (richiede `connection_id` salvato).

**Endpoint:** `POST /api/users/{id}/sync`

**cURL:**
```bash
curl --location --request POST 'http://localhost:8080/api/users/user_001/sync'
```

---

## 📊 Struttura Database

L'applicazione utilizza le seguenti tabelle:

1.  **`users`**: Anagrafica utente sincronizzata con Keycloak e SaltEdge (`customer_id`, `connection_id`).
2.  **`bank_account`**: Conti correnti recuperati da SaltEdge o creati localmente.
3.  **`transaction`**: Movimenti bancari associati ai conti.
4.  **`tax`**: Scadenze fiscali dell'utente.

---

## ⚙️ Configurazione

Configurare le credenziali SaltEdge in `src/main/resources/application.properties`:

```properties
saltedge.app-id=IL_TUO_APP_ID
saltedge.secret=IL_TUO_SECRET
```

## 🛠️ Build e Run

All'avvio, l'applicazione popola automaticamente il database in-memory con alcuni dati di test:
- **`user_001`**: Utente con `customer_id` e `connection_id` fittizi (pronto per il sync).
- **`user_002`**: Utente "pulito", senza integrazioni SaltEdge (pronto per la creazione customer).

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 🛢️ H2 Console
È possibile accedere alla console del database in-memory per scopi di debug:
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** `123`
