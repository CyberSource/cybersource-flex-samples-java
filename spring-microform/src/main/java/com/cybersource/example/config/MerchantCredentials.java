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
//        Properties props = new Properties();
//        props.setProperty("merchantID", merchantID);
//        props.setProperty("merchantKeyId", merchantKeyId);
//        // Take care, not true camel case here
//        props.setProperty("merchantsecretKey", merchantSecretKey);
//        props.setProperty("userAgent", userAgent);
//        props.setProperty("requestHost", requestHost);
//        props.setProperty("runEnvironment", runEnvironment);
//        props.setProperty("authenticationType", authenticationType);
//
//        return props;

        Properties props = new Properties();

        // HTTP_Signature = http_signature and JWT = jwt
        props.setProperty("authenticationType", "http_signature");
        props.setProperty("merchantID", "testrest");
        props.setProperty("runEnvironment", "apitest.cybersource.com");
        props.setProperty("requestJsonPath", "src/main/resources/request.json");

        // MetaKey Parameters
        props.setProperty("portfolioID", "");
        props.setProperty("useMetaKey", "false");

        // JWT Parameters
        props.setProperty("keyAlias", "testrest");
        props.setProperty("keyPass", "testrest");
        props.setProperty("keyFileName", "testrest");

        // P12 key path. Enter the folder path where the .p12 file is located.

        props.setProperty("keysDirectory", "src/main/resources");
        // HTTP Parameters
        props.setProperty("merchantKeyId", "08c94330-f618-42a3-b09d-e1e43be5efda");
        props.setProperty("merchantsecretKey", "yBJxy6LjM2TmcPGu+GaJrHtkke25fPpUX+UY6/L/1tE=");
        // Logging to be enabled or not.
        props.setProperty("enableLog", "true");
        // Log directory Path
        props.setProperty("logDirectory", "log");
        props.setProperty("logFilename", "cybs");

        // Log file size in KB
        props.setProperty("logMaximumSize", "5M");

        // OAuth related properties.
        props.setProperty("enableClientCert", "false");
        props.setProperty("clientCertDirectory", "src/main/resources");
        props.setProperty("clientCertFile", "");
        props.setProperty("clientCertPassword", "");
        props.setProperty("clientId", "");
        props.setProperty("clientSecret", "");

		/*
		PEM Key file path for decoding JWE Response Enter the folder path where the .pem file is located.
		It is optional property, require adding only during JWE decryption.
		*/
        props.setProperty("jwePEMFileDirectory", "src/main/resources/NetworkTokenCert.pem");

        return props;
    }

}
