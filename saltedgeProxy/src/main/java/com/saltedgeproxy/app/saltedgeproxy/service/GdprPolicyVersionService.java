package com.saltedgeproxy.app.saltedgeproxy.service;

import com.saltedgeproxy.app.saltedgeproxy.config.GdprPolicyProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GdprPolicyVersionService {

    private final GdprPolicyProperties gdprPolicyProperties;

    public GdprPolicyVersionService(GdprPolicyProperties gdprPolicyProperties) {
        this.gdprPolicyProperties = gdprPolicyProperties;
    }

    public String getCurrentVersion() {
        return requireCurrentVersion();
    }

    public void validateCurrentVersion(String submittedVersion) {
        String currentVersion = requireCurrentVersion();

        if (!StringUtils.hasText(submittedVersion) || !currentVersion.equals(submittedVersion)) {
            throw new InvalidPolicyVersionException(submittedVersion, currentVersion);
        }
    }

    private String requireCurrentVersion() {
        String currentVersion = gdprPolicyProperties.getCurrentVersion();

        if (!StringUtils.hasText(currentVersion)) {
            throw new IllegalStateException("Current GDPR policy version is not configured.");
        }

        return currentVersion;
    }
}
