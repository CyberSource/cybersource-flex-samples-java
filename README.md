# CyberSource Flex Samples (Java)

This repository provides simple examples demonstrating usage of the CyberSource Flex SDK using either a headless JavaScript call (jsp-flexjs) or a fully customizable hosted field/microform which is incorporated into your checkout page.  For more details on Secure Acceptance Flex visit our Developer Guide at https://developer.cybersource.com/api/developer-guides/dita-flex/SAFlexibleToken.html

## Usage

1. Clone or download this repository.
2. Follow the application-specific directions to package and run either [jsp-flexjs](./jsp-flexjs/README.md) or [spring-microform](./spring-microform/README.md)
3. Browse to the checkout page.  E.g. http://localhost:8080/jsp-microform-1.0 (Flex API) or http://localhost:8080 (Microform) 

## Requirements
* Java 1.8 or later 
* Tomcat web server

**NOTE: We also have samples for Flex available in .NET, PHP & Node.js**

## API Reference
While these examples use the JavaScript libraries which we recommend as the most convenient option, you can try out the APIs behind the JavaScript SDKs by visiting our API Reference at https://developer.cybersource.com/api-reference-assets/index.html#flex


## Setting Your API Credentials

To set your API credentials for an API request, configure the following information:
  
* [Flex API](./jsp-flexjs/src/main/webapp/WEB-INF/credentials.properties)
  ```properties
  merchantID = your_merchant_id
  keyId = your_key_serial_number
  sharedSecret = your_key_shared_secret
  ```
* Microform (./spring-microform/src/main/resources/application-local.properties not checked into source control for security)

  ```properties
  app.merchantID= your_merchant_id
  app.merchantKeyId= your_key_serial_number
  app.merchantSecretKey= your_key_shared_secret
  ```

## Background on PCI-DSS

Storing your customer’s card data can dramatically increase your repeat-customer conversion rate, but can also add additional risk and [PCI DSS](https://www.pcisecuritystandards.org/pci_security/) overhead. You can mitigate these costs by tokenizing card data. CyberSource will store your customer’s card data within secure Visa data centers, replacing it with a token that only you can use. 

Secure Acceptance Flexible Token is a secure method for Tokenizing card data, that leaves you in total control of the customer experience. Your customer’s card number is encrypted on their own device - for example inside a browser or native app - and sent directly to CyberSource. This means card data bypasses your systems altogether. This can help you qualify for [SAQ A](https://www.pcisecuritystandards.org/documents/Understanding_SAQs_PCI_DSS_v3.pdf) based PCI DSS assessments for web-based integrations, and [SAQ A-EP](https://www.pcisecuritystandards.org/documents/Understanding_SAQs_PCI_DSS_v3.pdf) for native app integrations.

You are in total control of the look and feel, with the ability to seamlessly blend the solution in to your existing checkout flow, on web or in-app.

On-device encryption helps to protect your customers from attacks on network middleware such as app accelerators, DLPs, CDNs, and malicious hotspots.

The token can be used in lieu of actual card data in server-side requests for other CyberSource services, for example to make a payment, using our REST APIs: https://developer.cybersource.com/api-reference-assets/index.html#payments_payments

## Samples

### JavaScript (Flex API) Sample

This sample demonstrates how your checkout form can remain exactly as it is today, with the only addition of a JavaScript call to tokenize the customer's credit card information. This happens directly between their browser and CyberSource, replacing the provided data with a secure PCI-compliant token. This can then be sent to your server along with the other non-PCI order data.  This can help achieve PCI-DSS SAQ A-EP level compliance for your application.  

### Microform Sample

This sample demonstrates how you can replace the sensitive data fields (credit card number) on your checkout form with a field (Flex Microform) hosted entirely on CyberSource servers. This field will accept and tokenize the customer's credit card information directly from their browser on a resource hosted by CyberSource, replacing that data with a secure PCI-compliant token. This can then be sent to your server along with the other non-PCI order data.  This can help achieve PCI-DSS SAQ-A level compliance for your application as even your client-side code does not contain a mechanism to handle the credit card information.

## Using the Flex Payment Token

You can use the token generated to make a payment with the CyberSource REST API (https://developer.cybersource.com/api-reference-assets/index.html#payments_payments).  

Place the token in the CustomerId field:

```json
{
  "clientReferenceInformation": {
    "code": "TC50171_3"
  },
  "orderInformation": {
    "amountDetails": {
      "totalAmount": "102.21",
      "currency": "USD"
    },
    "billTo": {
      "firstName": "John",
      "lastName": "Doe",
      "address1": "1 Market St",
      "locality": "san francisco",
      "administrativeArea": "CA",
      "postalCode": "94105",
      "country": "US",
      "email": "test@cybs.com",
      "phoneNumber": "4158880000"
    }
  },
  "tokenInformation": {
    "transientTokenJwt": "[PLACE YOUR FLEX TOKEN HERE]"
  }
}


```

