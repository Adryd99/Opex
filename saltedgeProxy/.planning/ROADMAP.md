# Roadmap: SaltEdge Proxy GDPR Consent

## Overview

This roadmap adds GDPR consent control to the existing Salt Edge activation flow in four backend-first phases. It establishes one authoritative policy version, enables consent capture against that version, blocks activation until valid consent exists, and exposes consent status for enforcement and operational verification.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

- [x] **Phase 1: Policy Baseline** - Establish the single policy-version source used by consent workflows.
- [ ] **Phase 2: Consent Capture** - Record GDPR consent for existing users with policy-version evidence.
- [ ] **Phase 3: Activation Enforcement** - Block Salt Edge activation unless current-version consent exists.
- [ ] **Phase 4: Consent Visibility** - Expose current consent status for verification and backend checks.

## Phase Details

### Phase 1: Policy Baseline
**Goal**: The service has one authoritative privacy-policy version for all consent decisions.
**Depends on**: Nothing (first phase)
**Requirements**: POLI-01, POLI-02
**Success Criteria** (what must be TRUE):
  1. Consent validation uses a single configured current privacy-policy version instead of request-side assumptions.
  2. Consent submissions that match the configured current policy version are accepted for processing.
  3. Consent submissions that do not match the configured current policy version are rejected before consent is stored.
**Plans**: 1

### Phase 2: Consent Capture
**Goal**: API clients can record GDPR consent evidence for an existing user before activation.
**Depends on**: Phase 1
**Requirements**: CONS-01, CONS-02, CONS-03
**Success Criteria** (what must be TRUE):
  1. API client can record GDPR consent for an existing user before activation is attempted.
  2. Stored consent includes the accepted privacy-policy version for that user.
  3. Stored consent includes the timestamp when the policy was accepted.
**Plans**: TBD

### Phase 3: Activation Enforcement
**Goal**: Salt Edge activation is allowed only for users with valid current-version consent.
**Depends on**: Phase 2
**Requirements**: ENFC-01, ENFC-02
**Success Criteria** (what must be TRUE):
  1. `POST /api/users/{id}` rejects activation when no consent record exists for the user.
  2. `POST /api/users/{id}` allows activation only when the user has consent for the configured current policy version.
  3. Users with missing or outdated consent cannot reach Salt Edge activation setup.
**Plans**: TBD

### Phase 4: Consent Visibility
**Goal**: Backend clients can inspect a user's current consent state for enforcement and operational verification.
**Depends on**: Phase 3
**Requirements**: VIEW-01, VIEW-02
**Success Criteria** (what must be TRUE):
  1. API client can retrieve the current consent status for a user.
  2. The consent status response states whether consent exists.
  3. The consent status response includes the accepted policy version and acceptance timestamp when consent exists.
  4. Backend operators can use the status response to verify whether a user is ready for activation.
**Plans**: TBD

## Progress

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Policy Baseline | 1/1 | Complete | 2026-03-12 |
| 2. Consent Capture | 0/TBD | Not started | - |
| 3. Activation Enforcement | 0/TBD | Not started | - |
| 4. Consent Visibility | 0/TBD | Not started | - |
