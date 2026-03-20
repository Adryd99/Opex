package com.saltedgeproxy.app.saltedgeproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SaltedgeproxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaltedgeproxyApplication.class, args);
    }

}
