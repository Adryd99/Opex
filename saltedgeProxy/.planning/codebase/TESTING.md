# Testing Reference

## Current Test Stack
- The project depends on `org.springframework.boot:spring-boot-starter-test` in `pom.xml`.
- The only committed test class is `src/test/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplicationTests.java`.
- That test uses JUnit 5 with `@Test` and `@SpringBootTest`.
- There is no evidence of Mockito, MockMvc, WireMock, Testcontainers, AssertJ custom usage, or JaCoCo configuration in `pom.xml` or `src/test`.

## Current Test Shape
- The suite is a single context smoke test: `contextLoads()`.
- Because it is a full `@SpringBootTest`, it exercises Spring Boot auto-configuration rather than a narrow slice.
- No controller endpoint behavior is asserted.
- No repository queries are asserted.
- No Salt Edge API client behavior is asserted.
- No entity mapping or persistence relationships are asserted.

## How Tests Are Intended To Run
- The standard project command is Maven wrapper based: `./mvnw test` from the repository root.
- On Windows PowerShell, `.\\mvnw.cmd test` is the direct equivalent.
- In this environment, the wrapper could not complete because it tried to create or use Maven state outside the writable sandbox and then download Maven over restricted network access.
- The observed failures were:
- `Could not create local repository at C:\Users\CodexSandboxOffline\.m2\repository`
- after redirecting `MAVEN_USER_HOME`, the wrapper then failed during download with a closed underlying connection.
- Practically, that means the test command is repository-standard, but I could not complete a fresh execution inside this sandboxed session.

## Effective Runtime Under Test
- `src/main/resources/application.properties` is the only visible Spring properties file; there is no `src/test/resources/application.properties`.
- Tests therefore appear to inherit production-like defaults unless overridden inline.
- The configured datasource is file-backed H2 at `jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE`.
- Existing H2 files already live under `data/`, including `data/testdb.mv.db`.
- `spring.jpa.hibernate.ddl-auto=update` means a full context test can mutate schema state on startup.
- `DataInitializer.java` registers a `CommandLineRunner`, so full application tests may seed records as part of context initialization.

## Mocking and Stubbing Posture
- There is currently no mocking or stubbing infrastructure checked into `src/test`.
- `UserController.java` depends directly on repositories and `SaltEdgeService` via field injection.
- `SaltEdgeService.java` instantiates `RestTemplate` internally with `new RestTemplate()`, which makes outbound HTTP harder to replace in tests than a bean-injected client would be.
- Because Salt Edge credentials have fallback default values in `application.properties`, any future integration-style test that reaches `SaltEdgeService` without mocking could attempt real external calls.
- There are no fake DTO payload fixtures, JSON fixtures, or reusable test builders in the repository.

## Coverage Posture
- Coverage is effectively minimal.
- The only known automated check is whether the application context boots.
- There is no coverage plugin, no threshold enforcement, and no report artifact configured in `pom.xml`.
- The most business-critical method, `UserController.syncUserData(...)`, has no direct tests even though it performs nested account and transaction synchronization with database writes.
- `SaltEdgeService.java` has zero automated verification for headers, URLs, request bodies, or error propagation.

## Major Testing Gaps
- Controller contract gaps: no tests for `POST /api/users/{id}`, `DELETE /api/users/{id}`, or `POST /api/users/{id}/sync` in `UserController.java`.
- Persistence gaps: no tests for repository-derived queries in `UserRepository.java`, `BankAccountRepository.java`, or `TransactionRepository.java`.
- Mapping gaps: no tests for `@JsonProperty` DTO mappings in `SaltEdgeCustomerResponse.java`, `SaltEdgeAccountResponse.java`, `SaltEdgeConnectResponse.java`, or `SaltEdgeTransactionResponse.java`.
- Data safety gaps: no tests proving `DataInitializer.java` seeds expected records only once or behaves safely with the file-backed H2 database.
- Failure-path gaps: no tests for missing users, null Salt Edge responses, HTTP failures, duplicate account sync, or duplicate transaction sync.
- Isolation gaps: no dedicated test properties to switch from `data/testdb` to in-memory or temporary test storage.

## High-Value Next Tests
- Add repository slice tests with `@DataJpaTest` for the custom finder methods in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/repository`.
- Add controller tests around `UserController.java`, ideally with mocked service behavior and deterministic repository state.
- Add focused service tests around `SaltEdgeService.java` once the HTTP client is injectable or wrapped so requests can be stubbed.
- Add a test-specific properties file under `src/test/resources` to isolate the datasource and suppress accidental use of long-lived local DB files.
- Add negative-path tests for missing users and upstream Salt Edge failure scenarios before extending feature scope.
