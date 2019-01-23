# Flex Microform Sample

Flex Microform is a CyberSource-hosted HTML/Javascript component that replaces the card number input field on your checkout page and calls the Flex API on your behalf. This simple example integration demonstrates using the Flex Microform SDK to embed this PCI SAQ-A level component in your form. For more details on this see our Developer Guide at https://developer.cybersource.com/api/developer-guides/dita-flex/SAFlexibleToken/FlexMicroform.html

## Prerequisites

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [JCE unlimited policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- [Maven](https://maven.apache.org/install.html)
- [Tomcat 8 Web Server](http://tomcat.apache.org)

## Setup Instructions

1. Modify `./src/main/webapp/credentials.properties` with the Cybersource REST credentials created through [EBC Portal](https://ebc2.cybersource.com/).

  ```
  merchantId=YOUR MERCHANT ID
  keyId=YOUR KEY ID
  sharedSecret=YOUR SHARED SECRET
  ```

2. Build and run the application using maven
  ```bash
  mvn clean install
  ```

  This will produce a `.war` file that can be deployed to a Tomcat server instance. The deployed application will serve a demonstration card tokenization page on `http://localhost:8080/`. To serve from a different domain, ensure that `targetOrigin` domain is specified when making a call to the `/keys` endpoint. For a detailed example please see [FlexKeyProvider.java](./src/main/java/com/cybersource/example/FlexKeyProvider.java), line 47.

## Tips

- If you are having issues, checkout the full [Hosted FLEX documentation](https://developer.cybersource.com/api/developer-guides/dita-flex/SAFlexibleToken/FlexMicroform.html).

- If the application throws `java.security.InvalidKeyException: Illegal key size` you have probably not installed the [JCE unlimited policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).

- Safari version 10 and below does not support `RsaOaep256` encryption schema, for those browser please specify encryption type `RsaOaep` when making a call to the `/keys` endpoint.  For a detailed example please see [FlexKeyProvider.java](./src/main/java/com.cybersource/example/FlexKeyProvider.java), line 47.
