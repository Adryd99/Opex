# Codebase Concerns

## Overview
- The repository is a small Spring Boot proxy, but a large share of business flow, persistence orchestration, and third-party integration logic is concentrated in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`.
- Runtime configuration in `src/main/resources/application.properties` is currently closer to a local demo profile than a production-safe baseline, and there is no profile separation visible in the repository.
- Verification depth is low: `src/test/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplicationTests.java` only checks that the Spring context starts, and even that could not be fully validated here because Maven compilation failed in the current environment due to a missing JDK compiler.

## Security Concerns
- `src/main/resources/application.properties` includes default Salt Edge credentials and an H2 password fallback. Even if these are test values, checking defaults into the main config normalizes secret leakage and increases the risk of accidental real-environment use.
- `src/main/resources/application.properties` enables the H2 console unconditionally and uses a file database under `data/testdb`. That exposes a browser-accessible admin surface in any environment where the app starts without stronger perimeter controls.
- There is no sign of `spring-boot-starter-security`, request authentication, authorization, or API signing in `pom.xml` or `src/main/java`. All exposed user endpoints in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` appear publicly callable.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` accepts path-driven mutations with no validation of caller identity versus the user being modified. In its current form, anyone who can reach the service can activate, deactivate, or sync arbitrary users by ID.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java` writes request activity to `System.out` instead of a structured logger. It is not currently printing secrets, but the pattern makes future accidental credential or PII logging more likely.

## Bug Risk
- The create flow in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` only creates a Salt Edge customer when `customerId == null && !isActive`. That couples onboarding to a mutable activation flag and makes user state transitions fragile.
- The same method returns a connect URL after `connectConnection(...)`, but there is no visible callback or webhook endpoint anywhere under `src/main/java` to persist the eventual `connection_id`. The application depends on `connectionId` for later sync, so the integration flow looks incomplete.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` marks the user active even when customer creation or connect initiation falls through without a usable connect URL. That can leave a locally "active" user in a half-onboarded state.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` performs multi-step sync without `@Transactional`. If account saves succeed and a later Salt Edge call or transaction save fails, local state can be partially updated.
- Deletion in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` is only a soft delete. Meanwhile `removeConnection(...)` and `removeCustomer(...)` exist in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java` but are unused, which suggests remote Salt Edge records may be orphaned indefinitely.
- The sync logic upserts accounts and transactions that are returned by Salt Edge, but never removes data that disappeared remotely. Closed accounts, deleted transactions, or disconnected users can remain stale in the local database.
- Several state fields are nullable wrapper booleans in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/User.java`, `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/BankAccount.java`, and `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/Tax.java`. The code already contains ad hoc null handling, which is a sign that tri-state bugs are possible.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java` comments say `user_002` is new and not connected, but the seeded object is created with both `customerId` and `connectionId`. That mismatch is a concrete sign that docs, assumptions, and code paths are already drifting apart.

## Performance And Availability
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` makes one Salt Edge transaction request per account in a synchronous loop. User sync latency will scale linearly with account count, and one slow account blocks the entire request thread.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java` constructs a raw `RestTemplate` inline with no configured timeouts, retry policy, backoff, connection pooling strategy, or resilience layer. A slow upstream can consume servlet threads for too long.
- There is no visible rate-limit handling, pagination handling, or batching in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/SaltEdgeService.java`. Large customer histories may become expensive to sync or silently incomplete depending on upstream defaults.
- `src/main/resources/application.properties` uses `spring.jpa.hibernate.ddl-auto=update` against a persistent file-backed H2 database. That is convenient locally but makes schema evolution harder to reason about and increases startup-time mutation risk.

## Maintainability Hotspots
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` mixes HTTP concerns, onboarding workflow, external API orchestration, data mapping, and persistence writes in one class. This is the main maintainability hotspot in the repository.
- The Salt Edge payload mapping is hand-coded directly inside controller methods instead of being isolated in mapper or service classes. That will make contract changes harder to review and test.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/DataInitializer.java` runs unconditionally at startup. Seeding fake users and transactions in the main runtime path will keep polluting environments unless this is gated behind a profile.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/User.java` contains fields like `answer1` through `answer5`, while `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/Tax.java` has no repository, service, controller, or test usage. The domain model appears partially scaffolded and not yet governed by a clear lifecycle.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/model/Transaction.java` maps to a table named `transaction`, which is a portability hazard because that identifier is reserved or awkward in several SQL dialects.
- `README.md` contains visible character-encoding corruption, which lowers trust in the written documentation and makes onboarding details easier to misunderstand.

## Testing Gaps
- There are no repository tests, controller tests, contract tests for Salt Edge payloads, or sync workflow tests under `src/test/java`.
- There is no evidence of mocked upstream failure testing for cases like Salt Edge timeouts, malformed payloads, or partial sync responses.
- There are no assertions around data seeding, soft delete semantics, duplicate sync idempotency, or state repair after a failed onboarding attempt.
- Maven test execution was attempted, but compilation stopped before tests because the current environment lacks a JDK compiler. That leaves the current executable health partially unknown even for the minimal existing test suite.

## Important Unknowns
- It is unclear whether another system is expected to write `customerId` and `connectionId` back into `users`. No such callback path is present in this repository.
- It is unclear whether this repository is intended only for local prototyping or for a deployable service. Current defaults in `src/main/resources/application.properties` strongly suggest prototype assumptions, but the exposed HTTP API implies broader use.
- It is unclear what the authoritative schema and migration strategy should be. There are no migration files, and the application relies on Hibernate schema updates.
- It is unclear whether the committed runtime artifacts under `data/` are intentional fixtures or accidental local state. Their presence means behavior can vary between machines even before tests are added.
- It is unclear which user source of truth exists upstream. `README.md` references Keycloak-driven user provisioning, but this repository contains no Keycloak integration code.

## Highest-Value Follow-Ups
- Split onboarding and sync orchestration out of `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java` into transactional service methods with explicit error handling.
- Remove secret fallbacks and console defaults from `src/main/resources/application.properties`, and move local-only settings behind a dedicated profile.
- Add tests around onboarding, sync idempotency, remote failure handling, and seeded-data assumptions before extending the domain model further.
