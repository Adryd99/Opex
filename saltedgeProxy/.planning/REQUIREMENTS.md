# Requirements: SaltEdge Proxy GDPR Consent

**Defined:** 2026-03-12
**Core Value:** No user's Salt Edge activation should proceed without explicit, recorded GDPR consent tied to a policy version.

## v1 Requirements

### Consent Capture

- [ ] **CONS-01**: API client can record GDPR consent for an existing user through this service before Salt Edge activation occurs
- [ ] **CONS-02**: Consent submission stores the accepted privacy-policy version for that user
- [ ] **CONS-03**: Consent submission stores the timestamp when the user accepted the policy

### Policy Enforcement

- [ ] **ENFC-01**: `POST /api/users/{id}` rejects activation when the user has no recorded consent
- [ ] **ENFC-02**: Activation logic accepts users only when a consent record exists with the current policy version

### Consent Visibility

- [ ] **VIEW-01**: API client can retrieve a user's current consent status for backend enforcement or operational verification
- [ ] **VIEW-02**: Consent status response includes whether consent exists, the accepted policy version, and the acceptance timestamp

### Policy Configuration

- [x] **POLI-01**: Service has a single configured current privacy-policy version used during consent validation
- [x] **POLI-02**: Consent recording validates against the configured current privacy-policy version rather than hardcoded request assumptions

## v2 Requirements

### Consent Lifecycle

- **LIFE-01**: User can withdraw previously granted consent
- **LIFE-02**: Service tracks consent history across multiple policy-version acceptances
- **LIFE-03**: Service requires re-acceptance when the privacy-policy version changes after an earlier acceptance

### GDPR Rights

- **RGHT-01**: User can request export of personal and financial data held by this service
- **RGHT-02**: User can request erasure of personal data that is no longer legally required to be retained

## Out of Scope

| Feature | Reason |
|---------|--------|
| Public privacy-policy website or CMS | This phase is backend-first and only needs the policy version as an enforcement input |
| Consent withdrawal endpoint | Keep v1 focused on initial acceptance and activation gating |
| Automatic re-consent workflow after policy updates | Useful later, but not required to make first-time activation compliant |
| Broader GDPR rights automation | Separate compliance workstream from the current consent milestone |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| CONS-01 | Phase 2 | Pending |
| CONS-02 | Phase 2 | Pending |
| CONS-03 | Phase 2 | Pending |
| ENFC-01 | Phase 3 | Pending |
| ENFC-02 | Phase 3 | Pending |
| VIEW-01 | Phase 4 | Pending |
| VIEW-02 | Phase 4 | Pending |
| POLI-01 | Phase 1 | Complete |
| POLI-02 | Phase 1 | Complete |

**Coverage:**
- v1 requirements: 9 total
- Mapped to phases: 9
- Unmapped: 0

---
*Requirements defined: 2026-03-12*
*Last updated: 2026-03-12 after Phase 1 Plan 1 completion*
