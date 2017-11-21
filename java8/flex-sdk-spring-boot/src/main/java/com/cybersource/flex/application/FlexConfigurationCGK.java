/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.application;

import com.cybersource.flex.sdk.FlexService;
import com.cybersource.flex.sdk.FlexServiceFactory;
import com.cybersource.flex.sdk.authentication.CGKCredentials;
import com.cybersource.flex.sdk.exception.FlexException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlexConfigurationCGK {

    @Value("${cgk.mid}")
    private String mid;
    @Value("${cgk.keyId}")
    private String keyId;
    @Value("${cgk.shared-secret}")
    private char[] sharedSecret;

    @Bean
    public FlexService flexService() {
        try {
            CGKCredentials cgkCredentials = new CGKCredentials(CGKCredentials.Environment.CAS, mid, keyId, sharedSecret);
            return FlexServiceFactory.createInstance(cgkCredentials);
        } catch (FlexException flexException) {
            throw new RuntimeException("Error when configuring Flex Server SDK", flexException);
        }
    }

}
