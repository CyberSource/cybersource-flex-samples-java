## Flex Token API

### Introduction

Storing your customer’s card data can dramatically increase your repeat-custom conversion rate, but can also add additional risk and PCI DSS overhead. This can be mitigated by tokenizing card data. CyberSource will store your customer’s card data, replacing it with a token that can only be used by you.

Flex Token API is our most secure method for Tokenizing card data. Your customer’s card number is encrypted on their own device, for example inside a browser or native app, and sent directly to CyberSource. This means card data bypasses your systems altogether, typically qualifying e-commerce only merchants for [SAQ A-EP](https://www.pcisecuritystandards.org/documents/Understanding_SAQs_PCI_DSS_v3.pdf).

On-device encryption helps to protect your customers from attacks on network middleware such as app accelerators, DLPs, CDNs, and malicious hotspots.

The token can be used in lieu of actual card data in server-side requests for other CyberSource services, such as [Payer Authentication](http://apps.cybersource.com/library/documentation/dev_guides/Payer_Authentication_SO_API/Payer_Authentication_SO_API.pdf), [Decision Manager](https://www.cybersource.com/products/fraud_management/), [Tax Services](http://apps.cybersource.com/library/documentation/dev_guides/Tax_SO_API/Tax_SO_API.pdf), and [Card Payments](http://apps.cybersource.com/library/documentation/dev_guides/CC_Svcs_SO_API/Credit_Cards_SO_API.pdf).

The Flex Token API is a JSON based RESTful service, consisting of two resources: **keys** and **tokens**.

#### Keys

Create a public key to encrypt the card data on your customer's device (a browser or a native app). This is an authenticated request when rendering your payment form or control.

#### Tokens

Create a token using the encrypted card data. This is an unauthenticated request from your customer's device. The endpoint supports [CORS](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing), so AJAX requests are supported from browser based implementations.

### Examples

These repositories consist of two example web implementations, in [Java](java) and [C#](dotnet). They consist of a basic server that requests the keys and serves a front end that collects, encrypts and transmits the card data directly to CyberSource. Please ensure you read [our license](LICENSE.md) prior to making use of the provided code.
