/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.application;

import com.cybersource.flex.sdk.FlexService;
import com.cybersource.flex.sdk.authentication.VDPCredentials;
import com.cybersource.flex.sdk.impl.FlexKeyServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlexConfiguration {

    @Value("${vdp.api-key}")
    private String apiKey;
    @Value("${vdp.shared-secret}")
    private char[] sharedSecret;

    @Bean
    public FlexService flexService() {
        VDPCredentials vdpCredentials = new VDPCredentials(VDPCredentials.Environment.SANDBOX, apiKey, sharedSecret);
        return new FlexKeyServiceImpl(vdpCredentials);
    }

}
