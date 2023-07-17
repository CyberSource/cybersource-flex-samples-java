/**
 * Copyright (c) 2023 by CyberSource
 */
package com.cybersource.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties("app")
public class MerchantCredentials {

    String merchantID;
    String requestHost;
    String merchantKeyId;
    String merchantSecretKey;
    String userAgent;
    String runEnvironment;
    String authenticationType;

    public Properties getAsJavaProps() {
        Properties props = new Properties();
        props.setProperty("merchantID", merchantID);
        props.setProperty("merchantKeyId", merchantKeyId);
        // Take care, not true camel case here
        props.setProperty("merchantsecretKey", merchantSecretKey);
        props.setProperty("userAgent", userAgent);
        props.setProperty("requestHost", requestHost);
        props.setProperty("runEnvironment", runEnvironment);
        props.setProperty("authenticationType", authenticationType);

        return props;
    }

}
