# Architecture

## Overview
- `saltedgeproxy` is a single Spring Boot application that exposes a narrow REST API for user activation, user deactivation, and Salt Edge data synchronization.
- The executable entry point is `src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java`.
- Runtime concerns are concentrated in one vertical slice: HTTP endpoints in `controller`, orchestration plus outbound HTTP in `service`, persistence in `repository`, and JPA entities in `model`.
- There is no separate application-service/domain-service split yet; `UserController` currently owns most request orchestration.

## Entry Points
- Process startup begins in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java` via `SpringApplication.run(...)`.
- Boot-time data seeding is registered in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java` as a `CommandLineRunner` bean.
- Public HTTP entry points are all in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` under `@RequestMapping("/api/users")`.
- Exposed endpoints are `POST /api/users/{id}`, `DELETE /api/users/{id}`, and `POST /api/users/{id}/sync`.

## Request Flow
- `POST /api/users/{id}` loads the user from `UserRepository`, conditionally creates a Salt Edge customer and connection URL, marks the user active, persists the update, and optionally triggers account and transaction synchronization if `connectionId` is already present.
- `DELETE /api/users/{id}` performs a soft delete by setting `User.isActive` to `false` and saving through `UserRepository`.
- `POST /api/users/{id}/sync` reloads the user, checks for `connectionId`, and delegates to the private `syncUserData(...)` helper inside `UserController`.
- `syncUserData(...)` calls `SaltEdgeService.getAccounts(...)`, upserts `BankAccount` records, then for each account calls `SaltEdgeService.getTransactions(...)` and upserts `Transaction` records.
- The controller returns simple `ResponseEntity` payloads: user id strings, connection URL strings, `204 No Content`, or `404 Not Found`.

## Layers And Responsibilities
- Controller layer: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` is the API adapter and also the main workflow coordinator.
- Service layer: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java` wraps outbound REST calls to `https://www.saltedge.com/api/v6` and centralizes Salt Edge authentication headers.
- Repository layer: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository/*.java` uses Spring Data JPA for persistence and simple derived queries.
- Model layer: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/*.java` defines JPA entities for `User`, `BankAccount`, `Transaction`, and `Tax`.
- DTO layer: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/dto/*.java` maps Salt Edge request/response payloads and isolates JSON field naming from entity classes.
- Config layer: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java` handles startup seeding only; there is no dedicated HTTP client or database configuration class yet.

## Core Abstractions
- `User` is the aggregate root for local identity and Salt Edge linkage through `customerId`, `connectionId`, and `isActive`.
- `BankAccount` represents imported accounts keyed by `saltedgeAccountId`, linked to a `User`, and tagged with `isSaltedge`.
- `Transaction` represents imported movements keyed by Salt Edge transaction id and linked to both `User` and `BankAccount`.
- `Tax` is modeled as a user-owned entity but is not yet involved in controller or service flows.
- `SaltEdgeService` is the sole abstraction over the external Salt Edge API and owns header creation, endpoint URLs, and `RestTemplate` usage.
- `SaltEdgeCustomerResponse`, `SaltEdgeConnectResponse`, `SaltEdgeAccountResponse`, and `SaltEdgeTransactionResponse` are transport-only DTOs used at the integration boundary.

## Persistence And Data Relationships
- `User` maps to table `users` in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/User.java`.
- `BankAccount` maps to table `bank_account` and has `@ManyToOne(fetch = LAZY)` to `User`.
- `Transaction` maps to table `transaction` and has `@ManyToOne(fetch = LAZY)` to both `User` and `BankAccount`.
- `Tax` maps to table `tax` and belongs to `User`, but no repository or endpoint currently operates on it.
- Repositories expose lightweight finder methods such as `findByIdentifier`, `findByCustomerId`, `findByConnectionId`, `findByUser_Id`, and `findByBankAccount_SaltedgeAccountId`.
- Upsert behavior is implemented imperatively in `UserController` by looking up existing `BankAccount` and `Transaction` records by id before saving.

## Configuration Flow
- Base application configuration lives in `src/main/resources/application.properties`.
- Spring Boot reads datasource, JPA, H2 console, and application name settings from that file during startup.
- Salt Edge credentials are injected into `SaltEdgeService` using `@Value("${saltedge.app-id}")` and `@Value("${saltedge.secret}")`.
- Credential properties resolve from environment variables `SALTEDGE_APP_ID` and `SALTEDGE_SECRET`, with inline fallback defaults present in `application.properties`.
- The datasource points to file-backed H2 storage at `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE`, so runtime state persists in the repository-local `data/` directory.
- `spring.jpa.hibernate.ddl-auto=update` means the schema is derived from JPA annotations and evolved automatically at startup.

## External Integration Flow
- Outbound HTTP uses a locally instantiated `RestTemplate` inside `SaltEdgeService`; there is no shared bean, timeout config, or retry policy.
- `createCustomer(...)` POSTs to `/customers` with a `data` wrapper around arbitrary customer fields.
- `connectConnection(...)` POSTs to `/connections/connect` and injects a `consent` object with scopes `accounts` and `transactions`.
- `getAccounts(...)` GETs `/accounts?connection_id=...`.
- `getTransactions(...)` GETs `/transactions?connection_id=...&account_id=...`.
- `removeConnection(...)` and `removeCustomer(...)` exist as outbound delete helpers, but are not currently called by controller flows.

## Notable Patterns
- The code follows conventional Spring package segmentation by technical concern rather than feature package boundaries.
- Dependency injection in `UserController` uses field-level `@Autowired`, while `SaltEdgeService` mixes field injection for config and manual instantiation for `RestTemplate`.
- Entities and DTOs use Lombok heavily (`@Getter`, `@Setter`, `@Builder`, `@Data`) to minimize boilerplate.
- JSON-to-Java field mapping is handled explicitly with `@JsonProperty` in DTOs instead of leaking external naming into entities.
- Boot-time sample data is essential for local development because the seeded `user_001` and `user_002` records are the expected starting points in `README.md` and `DataInitializer`.
- There is effectively no service-layer transaction boundary or exception translation yet; controller methods assume happy-path external calls and rely on null checks rather than structured error handling.
