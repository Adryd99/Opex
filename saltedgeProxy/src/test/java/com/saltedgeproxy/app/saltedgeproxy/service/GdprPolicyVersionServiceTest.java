package com.saltedgeproxy.app.saltedgeproxy.service;

import com.saltedgeproxy.app.saltedgeproxy.config.GdprPolicyProperties;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GdprPolicyVersionServiceTest {

    @Test
    void returnsConfiguredCurrentVersion() {
        String currentVersion = policyVersion();
        GdprPolicyVersionService service = createService(currentVersion);

        assertThat(service.getCurrentVersion()).isEqualTo(currentVersion);
    }

    @Test
    void acceptsMatchingSubmittedVersion() {
        String currentVersion = policyVersion();
        GdprPolicyVersionService service = createService(currentVersion);

        assertThatCode(() -> service.validateCurrentVersion(currentVersion))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsMismatchedSubmittedVersion() {
        String currentVersion = policyVersion();
        GdprPolicyVersionService service = createService(currentVersion);
        String submittedVersion = policyVersion();

        assertThatThrownBy(() -> service.validateCurrentVersion(submittedVersion))
                .isInstanceOf(InvalidPolicyVersionException.class)
                .hasMessageContaining(currentVersion)
                .hasMessageContaining(submittedVersion);
    }

    private static GdprPolicyVersionService createService(String currentVersion) {
        GdprPolicyProperties properties = new GdprPolicyProperties();
        properties.setCurrentVersion(currentVersion);
        return new GdprPolicyVersionService(properties);
    }

    private static String policyVersion() {
        return UUID.randomUUID().toString();
    }
}
