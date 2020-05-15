/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example;


import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.cybersource.authsdk.core.MerchantConfig;

import com.cybersource.example.MerchantCredentials;
import Api.KeyGenerationApi;
import Invokers.ApiClient;
import Invokers.ApiException;
import Model.FlexV1KeysPost200Response ;
import Model.GeneratePublicKeyRequest;

public class FlexKeyProvider {

    static GeneratePublicKeyRequest request;

    private static GeneratePublicKeyRequest getRequest() {
        request = new GeneratePublicKeyRequest();
        request.encryptionType("RsaOaep256");
        request.targetOrigin ("http://localhost:8080");
        return request;

    }

    public FlexKeyProvider()
    {

    }

    public String bindFlexKeyToSession(HttpSession session) throws Exception {

        String flexPublicKey = "NoKeyReturned";

        try{
            request = getRequest();

            Properties merchantProp = MerchantCredentials.getMerchantDetails();
            MerchantConfig merchantConfig = new MerchantConfig(merchantProp);       
                
            ApiClient apiClient = new ApiClient();

            apiClient.merchantConfig = merchantConfig;  
                
            KeyGenerationApi keyGenerationApi = new KeyGenerationApi(apiClient);

            FlexV1KeysPost200Response response = keyGenerationApi.generatePublicKey(request, "JWT");
            System.out.println("Response :" +response);

            String responseCode = apiClient.responseCode;
            String status = apiClient.status;
            flexPublicKey = response.getKeyId();

            System.out.println("ResponseCode :" +responseCode);
            System.out.println("Status :" +status);
        } catch (ApiException e) {

            e.printStackTrace();
        }

        session.setAttribute("FlexPublicKey", flexPublicKey);
        return flexPublicKey;
    }

    public boolean verifyTokenResponse(HttpSession session, String flexResponse) throws Exception {
        System.out.print(flexResponse);
        String flexPublicKey = session.getAttribute("FlexPublicKey").toString();
        return true;
    }

}
