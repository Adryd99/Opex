package com.saltedgeproxy.app.saltedgeproxy.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GdprPolicyPropertiesTest {

    @Test
    void bindsCurrentVersionFromConfiguration() {
        String configuredVersion = UUID.randomUUID().toString();
        MapConfigurationPropertySource propertySource = new MapConfigurationPropertySource(
                Map.of("gdpr.policy.current-version", configuredVersion));
        Binder binder = new Binder(propertySource);

        GdprPolicyProperties properties = binder.bind("gdpr.policy", Bindable.of(GdprPolicyProperties.class))
                .orElseThrow(() -> new IllegalStateException("GDPR policy properties should bind"));

        assertThat(properties.getCurrentVersion()).isEqualTo(configuredVersion);
    }
}
