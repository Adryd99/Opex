# Code Conventions

## Scope
- This repository is a small Spring Boot 4 application under the base package `com.saltedgeproxy.app.saltedgeproxy`.
- The entry point is `src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java`.
- Package layout is feature-light and layer-first rather than domain-first.

## Package Organization
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller` contains HTTP endpoints. Currently that is only `UserController.java`.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service` contains outbound integration logic. Currently that is only `SaltEdgeService.java`.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository` contains Spring Data JPA repositories such as `UserRepository.java`, `BankAccountRepository.java`, and `TransactionRepository.java`.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model` contains JPA entities: `User.java`, `BankAccount.java`, `Transaction.java`, and `Tax.java`.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/dto` contains request and response shapes for the Salt Edge API, for example `SaltEdgeAccountResponse.java` and `SaltEdgeTransactionResponse.java`.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config` contains startup wiring such as `DataInitializer.java`.

## Naming Patterns
- Types use standard Java PascalCase names: `User`, `BankAccount`, `SaltEdgeService`, `SaltEdgeTransactionResponse`.
- Repository methods follow Spring Data derived-query naming, including nested property navigation such as `findByUser_Id` in `BankAccountRepository.java` and `findByBankAccount_SaltedgeAccountId` in `TransactionRepository.java`.
- Controller request mappings are resource-oriented and live under `/api/users`; see `UserController.java`.
- Database column names are mostly snake_case via `@Column(name = "...")`, while Java fields remain camelCase.
- DTO fields prefer camelCase Java names with `@JsonProperty` bridges for Salt Edge snake_case payloads, for example `customerId` mapped from `customer_id` in `SaltEdgeCustomerResponse.java`.

## Implementation Style
- The codebase relies heavily on annotations instead of explicit boilerplate: `@RestController`, `@Service`, `@Repository`, `@Entity`, `@Configuration`, Lombok `@Getter/@Setter/@Builder`, and Jackson `@JsonProperty`.
- Entities are mutable and typically built with Lombok builders during seeding, then updated through setters during sync flows.
- Dependency injection is currently mixed toward field injection with `@Autowired` in `UserController.java`, while `DataInitializer.java` uses method parameter injection on the `@Bean` factory method.
- `SaltEdgeService.java` constructs its own `RestTemplate` inline with `new RestTemplate()` instead of exposing it as a Spring bean.
- Logging is currently done with `System.out.println(...)` in `UserController.java`, `SaltEdgeService.java`, and `DataInitializer.java` rather than a structured logger.

## Persistence Conventions
- JPA entities use explicit table names: `users`, `bank_account`, `transaction`, and `tax`.
- Relationships are modeled with `@ManyToOne` on child entities and `@OneToMany(mappedBy = ...)` on `User.java` and `BankAccount.java`.
- Foreign key columns are explicit, for example `user_id` and `bank_account_id`.
- Primary keys are application-assigned strings, not generated numeric IDs. `User.id`, `Transaction.id`, and `BankAccount.saltedgeAccountId` all follow this pattern.
- Boolean fields use `is...` names in Java and typically map to snake_case columns such as `is_active`, `is_for_tax`, `is_saltedge`, and `is_external`.
- `User.java` uses `@Builder.Default` to default `isActive` to `false`; the rest of the entities do not define comparable defaults.

## Error Handling and Control Flow
- Controller methods use `ResponseEntity` directly and resolve missing rows with `ResponseEntity.notFound().build()`.
- Repository lookups are handled with `Optional`, either via explicit `isEmpty()` checks or functional `map(...).orElse(...)` branches in `UserController.java`.
- There is no centralized exception handling layer such as `@ControllerAdvice`.
- `SaltEdgeService.java` does not catch `RestClientException` or translate remote failures into domain-specific exceptions, so external API failures will currently bubble up as framework exceptions.
- The sync path in `UserController.java` is tolerant of null upstream bodies and silently skips work when `getData()` is null.

## Configuration Conventions
- Runtime configuration lives in `src/main/resources/application.properties`.
- The application name, datasource, JPA settings, H2 console toggle, and Salt Edge credentials are all configured in that single properties file.
- Secret values are wired through property placeholders with environment fallbacks: `saltedge.app-id=${SALTEDGE_APP_ID:...}` and `saltedge.secret=${SALTEDGE_SECRET:...}`.
- The default datasource is file-backed H2 at `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE`, not an in-memory database.
- Schema generation uses `spring.jpa.hibernate.ddl-auto=update`, so entity changes are expected to evolve the schema automatically during local runs.

## Data and Mapping Conventions
- Local entities are used as the persistence model only; outbound Salt Edge responses are first deserialized into dedicated DTOs in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/dto`.
- Mapping from external DTOs to entities is done manually inside `UserController.syncUserData(...)` instead of through a dedicated mapper component.
- Some field normalization is embedded inline during sync, for example transaction `type` is inferred from the sign of `amount`, and `merchant_name` is extracted from the `extra` map.
- `DataInitializer.java` seeds representative rows with builders and hard-coded IDs, emails, connection IDs, and balances.

## Practical Implications For Future Changes
- New web behavior should currently fit the existing controller-service-repository split unless the codebase is intentionally being refactored.
- New Salt Edge payload shapes should follow the DTO pattern already used in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/dto`.
- If a field exists in the database as snake_case or in Salt Edge as snake_case, the current convention is to keep Java camelCase and add `@Column` or `@JsonProperty` mappings instead of renaming the Java field.
- Because there is no shared mapper, validator, or exception layer yet, cross-cutting behavior added in one endpoint will need to be applied manually to peers unless those abstractions are introduced first.
