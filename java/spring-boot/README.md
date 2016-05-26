# Flex Java Example

A minimalist java/spring-boot example integration using Flex-API tokenization.

## Prerequisites

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [JCE unlimited policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- [Maven](https://maven.apache.org/install.html)

## Setup Instructions

1. Modify `./src/main/resources/application.properties` with the credentials provided by customer support in your private BETA invite and that your `.p12` key is placed in the directory specified.

  ```properties
  mid=YOUR_MID
  profileId=YOUR_PROFILE
  cmmKey=123456789012345678901234567890
  organizationId=YOUR_ORD_ID
  keyStoreFile=/your_keystore_file.p12
  keyStorePassword=YOUR_KEYSTORE_PASS
  privateKeyPassword=YOUR_PRIVKEY_PASS
  ```

2. Build and run the application using maven
  ```bash
  mvn clean spring-boot:run
  ```
  This will serve the application from [https://localhost:8443](https://localhost:8443).

## Tips

- If you are having issues, checkout the full [FLEX documentation](http://apps.cybersource.com/library/documentation/dev_guides/Secure_Acceptance_Flex/html/).

- To change the port the application is served on, update `server.port=8443` in `application.properties`, replacing `8443` with your desired port number.

- If the application throws `java.security.InvalidKeyException: Illegal key size` you have probably not installed the [JCE unlimited policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).

## Browser Support

- Chrome 37+
- Firefox 34+
- Edge 12+
- Opera 24+
- Internet Explorer 11+
- Safari 8+

*NB: IE11 and Safari support requires the use of polyfills for both [webcrypto](https://github.com/vibornoff/webcrypto-shim) and [promises](https://github.com/Zolmeister/promiz) (as demonstrated on the checkout page in this example).*

## Disclaimer

This respository is provided as a learning aid for merchants wishing to integrate with the Flex API.  The code samples are not production ready and are intended for illustrative purposes only. As such, any use of these code samples in a production setting is strongly discouraged. Any usage of these code samples must comply with the license agreement as defined in `LICENSE.md` at the root level of this repository.