package com.cybersource.example.service;

import Api.PaymentsApi;
import Invokers.ApiClient;
import Invokers.ApiException;
import Model.*;
import com.cybersource.authsdk.core.ConfigException;
import com.cybersource.authsdk.core.MerchantConfig;
import com.cybersource.example.config.MerchantCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentsService {
    @Autowired
    private final MerchantCredentials merchantCredentials;

    public String makePaymentWithTransientToken(final String transientToken) throws ConfigException, ApiException {
        final CreatePaymentRequest paymentRequest = createPaymentRequest(transientToken);
        final ApiClient apiClient = new ApiClient(new MerchantConfig(merchantCredentials.getAsJavaProps()));
        final PaymentsApi paymentApi = new PaymentsApi(apiClient);

        final PtsV2PaymentsPost201Response response = paymentApi.createPayment(paymentRequest);

        System.out.println("ResponseCode: " + apiClient.responseCode);
        System.out.println("Status: " + apiClient.status);
        System.out.println(response);

        return response.toString();
    }

    private static CreatePaymentRequest createPaymentRequest(final String transientToken) {

        final Ptsv2paymentsClientReferenceInformation client = new Ptsv2paymentsClientReferenceInformation()
                .code("test_payment");

        final Ptsv2paymentsOrderInformationBillTo billTo = new Ptsv2paymentsOrderInformationBillTo()
                .country("US")
                .firstName("John")
                .lastName("Deo")
                .address1("1 Market St")
                .postalCode("94105")
                .locality("san francisco")
                .administrativeArea("CA")
                .email("test@cybs.com");

        final Ptsv2paymentsOrderInformationAmountDetails amountDetails = new Ptsv2paymentsOrderInformationAmountDetails()
                .totalAmount("100.00")
                .currency("USD");

        final Ptsv2paymentsOrderInformation orderInformation = new Ptsv2paymentsOrderInformation()
                .billTo(billTo)
                .amountDetails(amountDetails);

        // Everything above is just normal payment information
        // The code below plugs in the transient token from Microform
        final Ptsv2paymentsTokenInformation tokenInformation = new Ptsv2paymentsTokenInformation().transientTokenJwt(transientToken);

        return new CreatePaymentRequest()
                .clientReferenceInformation(client)
                .orderInformation(orderInformation)
                .tokenInformation(tokenInformation);
    }
}
