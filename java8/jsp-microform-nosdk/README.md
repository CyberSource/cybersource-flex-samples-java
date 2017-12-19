# JSP (Tomcat) example using hosted Flex microform

A minimalist Java JSP example integration using Flex-API tokenization and Flex microform embedded card capture.

## Prerequisites

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [JCE unlimited policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- [Maven](https://maven.apache.org/install.html)
- [Tomcat 8 Web Server](http://tomcat.apache.org)

## Setup Instructions

1. Modify `./src/main/webapp/WEB-INF/credentials.properties` with the Cybersource Gate Keeper credentials created through [EBC Portal](https://ebc2.cybersource.com/).

  ```
  merchantId=YOUR MERCHANT ID
  keyId=YOUR KEY ID
  sharedSecret=YOUR SHARED SECRET
  ```

2. Build and run the application using maven
  ```bash
  mvn clean install
  ```

  This will produce a `.war` file that can be deployed to a Tomcat server instance.

## Tips

- If you are having issues, checkout the full [FLEX documentation](https://www.cybersource.com/developers/integration_methods/secure-acceptance-flexible-token/).

- If the application throws `java.security.InvalidKeyException: Illegal key size` you have probably not installed the [JCE unlimited policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).
