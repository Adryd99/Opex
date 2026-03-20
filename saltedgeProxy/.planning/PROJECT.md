# SaltEdge Proxy

## What This Is

`saltedgeproxy` is a Spring Boot backend that manages local users and synchronizes account and transaction data from Salt Edge into a local database. The current initiative is to extend that existing service with GDPR consent handling so end users' financial data is not activated through Salt Edge until the API has a recorded acceptance of the applicable privacy-policy version.

## Core Value

No user's Salt Edge activation should proceed without explicit, recorded GDPR consent tied to a policy version.

## Requirements

### Validated

- ✓ API can activate a local user and initiate Salt Edge connection setup through `POST /api/users/{id}` — existing
- ✓ API can synchronize Salt Edge accounts and transactions into local persistence through `POST /api/users/{id}/sync` — existing
- ✓ API can deactivate a user locally through `DELETE /api/users/{id}` — existing
- ✓ User, bank account, and transaction data are persisted locally with Spring Data JPA and H2 — existing

### Active

- [ ] User consent can be recorded by this API before Salt Edge activation occurs
- [ ] Consent records store at least accepted privacy-policy version and acceptance timestamp
- [ ] User activation is blocked when no valid consent record exists
- [ ] The service has a clear current privacy-policy version used when recording consent
- [ ] Consent state is retrievable for backend enforcement and operational verification

### Out of Scope

- Consent withdrawal or revocation flows — defer until initial acceptance flow is working
- Automatic re-acceptance workflows for future policy-version changes — defer beyond v1
- Broader GDPR data-subject rights such as export, rectification, or erasure — separate compliance scope
- Full legal-content management UI or public website policy publishing — not part of this backend-first phase

## Context

The existing system is a small Java 17 / Spring Boot application with a thin REST API, direct Salt Edge integration, and H2-backed persistence. The current request flow is centered in `src/main/java/com/saltedgeproxy/app/saltedgeproxy/controller/UserController.java`, where user activation can create Salt Edge customers and connection URLs and where sync can import account and transaction data into local tables. There is no authentication layer, frontend, or policy-management interface in this repository today, so GDPR work will need to fit a backend-only integration model.

The current codebase already stores financial data locally and includes seeded users via `DataInitializer`, which makes consent enforcement materially important before activation and sync continue to expand real user data handling. A codebase map exists under `.planning/codebase/`, so future phases should use those documents as reference when deciding where consent models, endpoints, and validation logic belong.

## Constraints

- **Tech stack**: Spring Boot 4, Java 17, Spring Data JPA, and H2 remain the implementation baseline — this work should fit the current application architecture
- **Integration boundary**: Salt Edge activation currently happens through `UserController` and `SaltEdgeService` — consent enforcement must not bypass that path
- **Product surface**: This repository has no UI or inbound auth stack today — v1 must work as an API-first backend capability
- **Scope**: Initial delivery is acceptance-only consent handling — avoid broad GDPR program expansion in the first roadmap

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Consent handling will be implemented in this API | The repository has no separate consent service and needs a local enforcement point before Salt Edge activation | — Pending |
| GDPR scope starts with consent and privacy-policy behavior | User clarified the first milestone is not full GDPR rights management | — Pending |
| Activation must be blocked until consent exists | This directly protects the core value of preventing unauthorized Salt Edge activation | — Pending |
| Consent evidence must include timestamp and policy version | Minimal compliance evidence needs to show when consent happened and what text/version was accepted | — Pending |
| v1 excludes withdrawal and policy re-acceptance flows | Keep the first phase narrow enough to implement and verify cleanly | — Pending |

---
*Last updated: 2026-03-12 after initialization*
