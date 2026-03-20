# Stack

- Primary language: Java 17, declared in `pom.xml`.
- Build system: Maven wrapper via `mvnw`, `mvnw.cmd`, and `.mvn/`.
- Application framework: Spring Boot 4.0.3 from the parent POM in `pom.xml`.
- Web layer: `org.springframework.boot:spring-boot-starter-web` in `pom.xml`.
- Persistence layer: `org.springframework.boot:spring-boot-starter-data-jpa` in `pom.xml`.
- Database runtime: H2 file-backed database via `com.h2database:h2` in `pom.xml`.
- Boilerplate reduction: Lombok with annotation processing configured in `pom.xml`.
- Tests: `org.springframework.boot:spring-boot-starter-test` with a single smoke test in `src/test/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplicationTests.java`.

## Runtime Layout

- Main entrypoint: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java`.
- Main REST controller: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`.
- External API client service: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java`.
- Startup seed logic: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java`.
- JPA entities: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/User.java`, `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/BankAccount.java`, `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/Transaction.java`, `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/Tax.java`.
- Spring Data repositories: `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository/UserRepository.java`, `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository/BankAccountRepository.java`, `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository/TransactionRepository.java`.
- Salt Edge response/request DTOs live under `src/main/java/com/saltedgeproxy/app/saltedgeproxy/dto/`.

## Configuration

- Main config file: `src/main/resources/application.properties`.
- App name: `spring.application.name=saltedgeproxy`.
- JDBC driver: `org.h2.Driver`.
- Active JDBC URL: `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE` from `src/main/resources/application.properties`.
- The live configuration is file-backed H2 under `data/`, not in-memory; this differs from the README examples in `README.md`.
- H2 console is enabled with `spring.h2.console.enabled=true`.
- Hibernate schema management is `spring.jpa.hibernate.ddl-auto=update`.
- No Spring profiles, YAML configs, or separate environment-specific config files were found.
- Salt Edge credentials are read from env-backed placeholders: `SALTEDGE_APP_ID` and `SALTEDGE_SECRET`.
- Default credential fallbacks are committed in `src/main/resources/application.properties`, so local startup does not require env vars but production hardening would.

## Startup Flow

- `SaltedgeproxyApplication.main` boots Spring and triggers component scanning from `src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java`.
- Spring loads `src/main/resources/application.properties`, configures the H2 datasource, JPA, MVC, and H2 console.
- `DataInitializer` registers a `CommandLineRunner` bean in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java`.
- On startup, that runner inserts two sample `User` rows, one sample `BankAccount`, and two sample `Transaction` rows.
- The controller exposes three endpoints under `/api/users` in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`.
- `POST /api/users/{id}` activates a user, optionally creates a Salt Edge customer/connect URL, and may trigger sync if a `connectionId` is already stored.
- `POST /api/users/{id}/sync` fetches accounts and transactions from Salt Edge and upserts them into local tables.
- `DELETE /api/users/{id}` performs a soft delete by flipping `isActive` to `false`.
- Local persistence uses repository interfaces only; there is no explicit service layer for local CRUD beyond the controller.

## Data Model Notes

- `User` is the aggregate root for `BankAccount`, `Transaction`, and `Tax` relationships in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/User.java`.
- `BankAccount` uses `saltedgeAccountId` as the primary key in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/BankAccount.java`.
- `Transaction` stores Salt Edge transaction ids directly as primary keys in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/Transaction.java`.
- `Tax` is an entity only; no repository, controller, or service currently uses it.
- `Transaction.bankAccount` joins on `bank_account_id`, while `BankAccount`'s id column is `saltedge_account_id`; this is worth validating against the generated schema.

## Useful Commands

- Windows dev run: `.\mvnw.cmd spring-boot:run`
- Unix-like dev run: `./mvnw spring-boot:run`
- Compile/package: `.\mvnw.cmd clean package`
- Run tests: `.\mvnw.cmd test`
- Build without tests: `.\mvnw.cmd -DskipTests package`
- Show dependency tree when auditing starters/transitives: `.\mvnw.cmd dependency:tree`
- The README run instructions are in `README.md`, but treat the database notes there as stale because the code now uses `jdbc:h2:file:./data/testdb`.

## Practical Observations

- The codebase is a small monolithic Spring Boot application with no module split beyond package folders under `src/main/java/com/saltedgeproxy/app/saltedgeproxy/`.
- HTTP client usage is the legacy synchronous `RestTemplate` instantiated directly inside `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java`.
- There is no dedicated configuration class for HTTP clients, no retry library, and no resilience framework in `pom.xml`.
- There are no migration tools such as Flyway or Liquibase in `pom.xml`; schema evolution currently depends entirely on Hibernate `ddl-auto=update`.
- There is no Dockerfile, Compose file, Gradle build, or CI config visible at the repository root.
