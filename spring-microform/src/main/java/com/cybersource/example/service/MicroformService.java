/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example.service;


import Api.MicroformIntegrationApi;
import Invokers.ApiClient;
import Invokers.ApiException;
import Model.GenerateCaptureContextRequest;
import com.cybersource.authsdk.core.ConfigException;
import com.cybersource.authsdk.core.MerchantConfig;
import com.cybersource.example.config.MerchantCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MicroformService {

    @Autowired
    private final MerchantCredentials merchantCredentials;

    public String generateCaptureContext(final GenerateCaptureContextRequest captureContextRequest) throws ConfigException, ApiException{

        final ApiClient apiClient = new ApiClient(new MerchantConfig(merchantCredentials.getAsJavaProps()));

        final MicroformIntegrationApi microformApi = new MicroformIntegrationApi(apiClient);

        final String response = microformApi.generateCaptureContext(captureContextRequest);

        System.out.println("Response Code: " + apiClient.responseCode);
        System.out.println("Response Message: " + apiClient.status);
        System.out.println("Response Body: " + response);
        return response;
    }
}
