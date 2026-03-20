---
phase: 01-policy-baseline
plan: 01
subsystem: api
tags: [spring-boot, configuration-properties, gdpr, testing]
requires: []
provides:
  - Typed GDPR policy-version configuration bound from application properties
  - Shared service for current-version lookup and submitted-version validation
  - Focused tests covering property binding and policy-version match or mismatch behavior
affects: [consent-capture, activation-enforcement]
tech-stack:
  added: []
  patterns:
    - Spring @ConfigurationProperties for GDPR policy settings
    - Shared service-based policy-version validation
key-files:
  created:
    - src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/GdprPolicyProperties.java
    - src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/GdprPolicyVersionService.java
    - src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/InvalidPolicyVersionException.java
    - src/test/java/com/saltedgeproxy/app/saltedgeproxy/config/GdprPolicyPropertiesTest.java
    - src/test/java/com/saltedgeproxy/app/saltedgeproxy/service/GdprPolicyVersionServiceTest.java
  modified:
    - src/main/resources/application.properties
    - src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java
key-decisions:
  - "Bootstrap configuration-properties scanning from the main application so policy configuration stays injectable without ad hoc @Value usage."
  - "Reject mismatched submitted policy versions through a dedicated InvalidPolicyVersionException so future consent persistence can fail before writing data."
patterns-established:
  - "GDPR policy version is read from typed Spring configuration instead of hardcoded request-side assumptions."
  - "Consent-related code should call GdprPolicyVersionService to validate policy versions before any persistence or activation logic."
requirements-completed: [POLI-01, POLI-02]
duration: 4 min
completed: 2026-03-12
---

# Phase 1 Plan 1: Policy Baseline Summary

**Typed Spring configuration for the current GDPR policy version with a shared validator service and focused binding or mismatch tests**

## Performance

- **Duration:** 4 min
- **Started:** 2026-03-12T16:48:15Z
- **Completed:** 2026-03-12T16:52:33Z
- **Tasks:** 3
- **Files modified:** 7

## Accomplishments
- Added `gdpr.policy.current-version` as the single authoritative privacy-policy version source and bound it through `GdprPolicyProperties`.
- Introduced `GdprPolicyVersionService` so future consent-capture code can read the current version and reject stale or unknown submissions before persistence.
- Added narrow tests proving property binding works and that matching or mismatched versions behave correctly.

## Task Commits

Each task was committed atomically:

1. **Task 1: Bind the current GDPR policy version through typed application configuration** - `e5a7d71` (feat)
2. **Task 2: Create reusable policy-version validation logic for future consent capture** - `fed1692` (feat)
3. **Task 3: Add focused tests for properties binding and version matching behavior** - `991dafe` (test)

## Files Created/Modified
- `src/main/resources/application.properties` - Defines the authoritative `gdpr.policy.current-version` value.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/SaltedgeproxyApplication.java` - Enables configuration-properties scanning from the application bootstrap.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/config/GdprPolicyProperties.java` - Exposes typed GDPR policy configuration for injection.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/GdprPolicyVersionService.java` - Supplies current-version lookup and reusable validation logic.
- `src/main/java/com/saltedgeproxy/app/saltedgeproxy/service/InvalidPolicyVersionException.java` - Defines the explicit mismatch failure contract.
- `src/test/java/com/saltedgeproxy/app/saltedgeproxy/config/GdprPolicyPropertiesTest.java` - Proves the Spring binder loads the configured policy version.
- `src/test/java/com/saltedgeproxy/app/saltedgeproxy/service/GdprPolicyVersionServiceTest.java` - Verifies match and mismatch behavior in the shared service.

## Decisions Made
- Enabled `@ConfigurationPropertiesScan` on the application bootstrap so future GDPR components can inject typed policy settings without duplicating property names.
- Used a dedicated runtime exception for invalid policy versions to give later consent-recording code a clear failure contract before any storage work runs.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 3 - Blocking] Pointed Maven verification at a local Java 17 JDK**
- **Found during:** Task 1 verification
- **Issue:** The default shell environment exposed only a Java 8 JRE, so `mvnw` could not compile or run tests because no compiler was available.
- **Fix:** Used the locally installed JDK at `C:\Users\adria\.jdks\ms-17.0.17` for Maven verification commands.
- **Files modified:** None
- **Verification:** `.\mvnw.cmd -q -DskipTests compile` passed and `.\mvnw.cmd test "-Dtest=GdprPolicyPropertiesTest,GdprPolicyVersionServiceTest"` passed.
- **Committed in:** Not committed (environment-only fix)

---

**Total deviations:** 1 auto-fixed (1 blocking)
**Impact on plan:** The deviation was limited to the verification environment and did not change repository scope or implementation behavior.

## Issues Encountered
None - plan completed as intended once the local Java 17 JDK was used for Maven verification.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Consent-capture work can inject `GdprPolicyProperties` or `GdprPolicyVersionService` instead of repeating property names or hardcoding policy assumptions.
- `GdprPolicyVersionService.validateCurrentVersion(...)` is ready to guard consent persistence against stale or unknown policy versions.
- Phase 1 is complete and ready for transition to consent capture work.

---
*Phase: 01-policy-baseline*
*Completed: 2026-03-12*

## Self-Check: PASSED
