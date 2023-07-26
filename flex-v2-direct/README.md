# Cybersource Flex Direct API Java example.

Live demo of this application is available here: https://flex-v2-java-direct-use-sample.appspot.com.

Flex Direct API allows merchants to write their own integration based on Transient Token concept.
For example Flex API can be used to isolate systems that capture *payment credentials* from systems that invoke *card services*.
Flex API facilitates keeping *payment credentials* away from merchant's backend processing, keeping those systems away from PCI compliance.

----

## Running the example code 

### Prerequisites

- Java 11 SDK
- Maven 3.6.3

### Setup Instructions

I. Clone or download this repo.

II. Modify ```FlexApiHeaderAuthenticator.java``` with the CyberSource REST credentials created through EBC Portal:

```java
private final String mid = "YOUR MERCHANT ID";
private final String kid = "YOUR KEY ID (SHARED SECRET SERIAL NUMBER)";
private final String secret = "YOUR SHARED SECRET";
```

III. Run sample application locally (in development mode):

```shell
$ mvn clean compile quarkus:dev
```

For details, please consult https://quarkus.io/guides/maven-tooling#dev-mode.

## Few technical details

### Technology stack

This application uses [QUARKUS](https://quarkus.io/) to provide framework for sample application.
Sample application leverages most popular Java standards and frameworks as:

- JAX-RS (RESTEasy) to implement
  - Server side HTTP endpoints used to process HTML forms.
  - Rest Client to Flex API endpoints:
    - ```GET /flex/v2/public-keys``` to retrieve Flex signing keys.
    - ```POST /flex/v2/sessions``` to create Capture Context.
    - ```POST /flex/v2/tokens``` to create Transient Token.
- jose4j to implement JWT(s) verification, JWE encryption and JWK operation.
- Qute for HTML rendering.

### Notable packages / classes

1. ```com.cybersource.samples.forms``` classes to facilitate HTML form POSTs.
2. ```com.cybersource.samples.handlers``` classes with business logic for Flex Direct API flow, namely:
   create Capture Context, capture sensitive information, prepare JWE encrypted payload, invoke tokenization.
3. ```FlexApiHeaderAuthenticator.java``` complete HTTP Signature authentication implementation that can be plugged to any JAX-RS client implementation as an ```@Provider```.
4. ```FlexApiPublicKeysResolver.java``` complete cryptographic Key provider for Jose4J that can retrieve and cache Flex API long living keys.

# Deployment to Google Cloud

```
mvn clean package appengine:deploy
```
