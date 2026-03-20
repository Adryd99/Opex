package com.saltedgeproxy.app.saltedgeproxy.service;

public class InvalidPolicyVersionException extends RuntimeException {

    public InvalidPolicyVersionException(String submittedVersion, String currentVersion) {
        super("Submitted GDPR policy version '%s' does not match current version '%s'."
                .formatted(submittedVersion, currentVersion));
    }
}
