# Integrations

## External APIs

- Salt Edge v6 is the only concrete outbound API integration in the codebase.
- The integration client is `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java`.
- Base URL is hardcoded as `https://www.saltedge.com/api/v6` in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java`.
- Authentication to Salt Edge uses HTTP headers `App-id` and `Secret`, populated from `saltedge.app-id` and `saltedge.secret`.
- The config keys come from `src/main/resources/application.properties` and are backed by env vars `SALTEDGE_APP_ID` and `SALTEDGE_SECRET`.
- Outbound client implementation is synchronous `RestTemplate`; there is no Feign client, WebClient, retry interceptor, or circuit breaker.
- Implemented Salt Edge calls:
- `POST /customers` via `createCustomer(...)`.
- `POST /connections/connect` via `connectConnection(...)`.
- `GET /accounts?connection_id=...` via `getAccounts(...)`.
- `GET /transactions?connection_id=...&account_id=...` via `getTransactions(...)`.
- `DELETE /connections/{connectionId}` via `removeConnection(...)`.
- `DELETE /customers/{customerId}` via `removeCustomer(...)`.
- Only the create/connect/accounts/transactions methods are used by `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`.
- The delete methods exist in the service but are not called anywhere in `src/main/java`.

## Storage

- Primary persistence is an H2 relational database configured in `src/main/resources/application.properties`.
- JDBC URL is `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE`, which creates local files under `data/`.
- Observed database artifacts already present: `data/testdb.mv.db`, `data/testdb.trace.db`, and `data/testdb.lock.db`.
- ORM is Spring Data JPA plus Hibernate from `spring-boot-starter-data-jpa` in `pom.xml`.
- Repository interfaces exist for users, bank accounts, and transactions in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository/`.
- Managed entities are `User`, `BankAccount`, `Transaction`, and `Tax` under `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/`.
- The `Tax` entity has no repository or service usage, so it is schema-only at the moment.
- No Redis, MongoDB, Postgres, MySQL, Elasticsearch, or object storage integration was found.
- No schema migration tool is present; storage shape evolves through `spring.jpa.hibernate.ddl-auto=update`.

## Inbound HTTP Surface

- The application exposes only REST endpoints from `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`.
- Base route is `/api/users`.
- `POST /api/users/{id}` reads a local user, may create a Salt Edge customer, may create a connect URL, and may sync local data.
- `DELETE /api/users/{id}` performs a local soft delete only.
- `POST /api/users/{id}/sync` pulls fresh accounts and transactions from Salt Edge into the local database.
- There are no webhook endpoints, callback controllers, SSE endpoints, GraphQL endpoints, or messaging consumers.
- There is no explicit inbound file upload handling and no `MultipartFile` usage in `src/main/java`.

## Auth And Identity

- There is no implemented Spring Security configuration in `src/main/java`.
- No `spring-boot-starter-security` dependency is declared in `pom.xml`.
- No JWT, OAuth2 resource server, session auth, API key filter, or method-level security annotations were found.
- `README.md` and comments in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` mention Keycloak as an upstream source of pre-created users, but no Keycloak client or token validation code exists here.
- Salt Edge credential auth is outbound-only and does not secure the app's own REST endpoints.

## Files And Local Side Effects

- Startup seeding is implemented by `CommandLineRunner` in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java`.
- That runner writes sample records into the configured H2 database on each application start.
- H2 console is enabled in `src/main/resources/application.properties`, exposing a browser-accessible debug console when the app is running.
- Build output is written under `target/`, including copied config at `target/classes/application.properties`.
- No direct CSV, JSON, XML, or binary file import/export workflow exists in the application code.

## Messaging, Async, And Eventing

- No Kafka, RabbitMQ, AMQP, JMS, SQS, or pub/sub integration was found in `pom.xml` or `src/main/java`.
- No scheduled jobs, `@Async`, Spring Integration flows, or background worker frameworks were found.
- All Salt Edge synchronization runs inline during the incoming HTTP request path in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`.

## Env-Driven Integrations

- `SALTEDGE_APP_ID` and `SALTEDGE_SECRET` are the only environment-driven integration variables found.
- Both env vars have default fallbacks committed directly in `src/main/resources/application.properties`.
- No other env-based endpoints, tokens, bucket names, brokers, or third-party credentials were found.

## Absent Integrations

- Webhooks: absent.
- Message brokers: absent.
- Cache layer: absent.
- Search engine: absent.
- Object/file storage service: absent.
- Email/SMS/push provider: absent.
- Metrics/tracing/observability SaaS integration: absent.
- External auth enforcement for inbound APIs: absent.
