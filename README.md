## Flex Token API

### Introduction

Storing your customer’s card data can dramatically increase your repeat-custom conversion rate, but can also add additional risk and [PCI DSS](https://www.pcisecuritystandards.org/pci_security/) overhead. You can mitigate these costs by tokenizing card data. CyberSource will store your customer’s card data within secure Visa data centers, replacing it with a token that only you can use. 

Secure Acceptance Flexible Token is a secure method for Tokenizing card data, that leaves you in total control of the customer experience. Your customer’s card number is encrypted on their own device, for example inside a browser or native app, and sent directly to CyberSource. This means card data bypasses your systems altogether. This can help you qualify for [SAQ A](https://www.pcisecuritystandards.org/documents/Understanding_SAQs_PCI_DSS_v3.pdf) based PCI DSS assessments for web based integrations, and [SAQ A-EP](https://www.pcisecuritystandards.org/documents/Understanding_SAQs_PCI_DSS_v3.pdf) for native app integrations.

You are in total control of the look and feel, with the ability to seamlessly blend the solution in to your existing checkout flow, on web or in-app.

On-device encryption helps to protect your customers from attacks on network middleware such as app accelerators, DLPs, CDNs, and malicious hotspots.

The token can be used in lieu of actual card data in server-side requests for other CyberSource services, such as [Payer Authentication](http://apps.cybersource.com/library/documentation/dev_guides/Payer_Authentication_SO_API/Payer_Authentication_SO_API.pdf), [Decision Manager](https://www.cybersource.com/products/fraud_management/), [Tax Services](http://apps.cybersource.com/library/documentation/dev_guides/Tax_SO_API/Tax_SO_API.pdf), and [Card Payments](http://apps.cybersource.com/library/documentation/dev_guides/CC_Svcs_SO_API/Credit_Cards_SO_API.pdf).

Secure Acceptance Flexible Token is a JSON based RESTful service, consisting of two resources: **keys** and **tokens**.

#### Keys

Create a transaction specific public key to encrypt the card data on your customer's device (a browser or a native app). This is an authenticated request from your server to CyberSource, when rendering your payment form or control.

We provide a [Java SDK](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cybersource%22%20AND%20a%3A%22flex-server-sdk%22) to simplify this, or you [call the Flexible Token API directly](http://apps.cybersource.com/library/documentation/dev_guides/hosted_flex/0_2_0/html/custom-server-integration/). SDKs for other languages will be available in future.


#### Tokens

Create a token using the encrypted card data.

For web based solutions use our Hosted JS library to replace the card number input field with a CyberSource ‘microform’ iframe. This microform can be styled to look and behave like a regular input field on your site, using CSS and event listeners.

For native application or IoT integrations, use the API integration. Encrypt the card number using the public key, and send as an unauthenticated request from your customer's device directly to CyberSource.

The resulting token can be verified server side using the public key. Our [Java SDK](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.cybersource%22%20AND%20a%3A%22flex-server-sdk%22) can be used to perform this function.


### Examples

These repositories consist of sample web implementations in [Java](https://github.com/CyberSource/cybersource-flex-samples/blob/master/java), using [Spring Boot](https://github.com/CyberSource/cybersource-flex-samples/tree/master/java8/flex-sdk-spring-boot) (Flex API) and [JSP](https://github.com/CyberSource/cybersource-flex-samples/tree/master/java8/jsp-microform) (Hosted). The SDK supports Java 7 and 8, for earlier versions see the "nosdk" examples. Further examples in other languages will be added in future.

The examples consist of a basic server that requests the keys and serves a front end that collects, encrypts and transmits the card data directly to CyberSource. Please ensure you read [our license](https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md) prior to making use of the provided code.

