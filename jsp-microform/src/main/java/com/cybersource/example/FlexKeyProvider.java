/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example;


import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import Api.PaymentsApi;
import Model.*;
import com.cybersource.authsdk.core.MerchantConfig;

import com.cybersource.example.MerchantCredentials;
import Api.KeyGenerationApi;
import Invokers.ApiClient;
import Invokers.ApiException;

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

    public String makePaymentWithTransientToken(HttpSession session, String flexResponse) throws Exception {
        System.out.print(flexResponse);

        CreatePaymentRequest request;
        request = new CreatePaymentRequest();

        Ptsv2paymentsClientReferenceInformation client = new Ptsv2paymentsClientReferenceInformation();
        client.code("test_payment");
        request.clientReferenceInformation(client);

        Ptsv2paymentsOrderInformationBillTo billTo = new Ptsv2paymentsOrderInformationBillTo();
        billTo.country("US");
        billTo.firstName("John");
        billTo.lastName("Deo");
        billTo.address1("1 Market St");
        billTo.postalCode("94105");
        billTo.locality("san francisco");
        billTo.administrativeArea("CA");
        billTo.email("test@cybs.com");

        Ptsv2paymentsOrderInformationAmountDetails amountDetails = new Ptsv2paymentsOrderInformationAmountDetails();
        amountDetails.totalAmount("100.00");
        amountDetails.currency("USD");

        Ptsv2paymentsOrderInformation orderInformation = new Ptsv2paymentsOrderInformation();
        orderInformation.billTo(billTo);
        orderInformation.amountDetails(amountDetails);
        request.setOrderInformation(orderInformation);

        // EVERYTHING ABOVE IS JUST NORMAL PAYMENT INFORMATION
        // THIS IS WHERE YOU PLUG IN THE MICROFORM TRANSIENT TOKEN
        Ptsv2paymentsTokenInformation tokenInformation = new Ptsv2paymentsTokenInformation();
        tokenInformation.transientTokenJwt(flexResponse);

        request.tokenInformation(tokenInformation);

        PtsV2PaymentsPost201Response response = null;

        try {

            /* Read Merchant details. */
            Properties merchantProp = MerchantCredentials.getMerchantDetails();
            MerchantConfig merchantConfig = new MerchantConfig(merchantProp);

            ApiClient apiClient = new ApiClient();

            apiClient.merchantConfig = merchantConfig;

            PaymentsApi paymentApi = new PaymentsApi(apiClient);
            response = paymentApi.createPayment(request);

            String responseCode = apiClient.responseCode;
            String status = apiClient.status;

            System.out.println("ResponseCode :" + responseCode);
            System.out.println("Status :" + status);
            System.out.println(response);

        } catch (ApiException e) {

            e.printStackTrace();
        }

        if (response != null) {
            return response.toString();
        }
        else {
            return "That didn't go so well";
        }
    }

}
