# Flex Microform Sample

A live demo of this application is available here: https://flex-mf-springboot-sample.appspot.com/.

Flex Microform is a CyberSource-hosted HTML/JavaScript component that replaces the card number input field on your checkout page
and calls the Flex API on your behalf. This simple example integration demonstrates using Flex Microform SDK to embed this
PCI SAQ-A level component in your form. For more details on this see our Developer Guide at:  
https://developer.cybersource.com/api/developer-guides/dita-flex/SAFlexibleToken/FlexMicroform.html

## Prerequisites
- [Java 14](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  (the application uses records but this could be refactored into standard POJOs easily)
- [Maven](https://maven.apache.org/install.html)


## Setup Instructions
1. Modify `./src/main/resources/application-local.properties` with CyberSource REST credentials created through the
   [EBC Portal](https://ebc2test.cybersource.com/). Learn more about how to get an account [here](https://developer.cybersource.com/hello-world.html).
  ```properties
  merchantId=YOUR MERCHANT ID
  keyId=YOUR KEY ID (SHARED SECRET SERIAL NUMBER)
  sharedSecret=YOUR SHARED SECRET
  ```
2. Replace the `targetOrigins` value in `./src/main/resources/capture-context-request.json` with the domain where Microform 
will be served (e.g.`http://localhost8080` if deploying locally). 
3. To deploy locally, simply build and run the application using Maven. Spring will automatically deploy a local Tomcat server with port 8080 exposed.
  ```bash
    mvn spring-boot:run
  ```

## Using the Application
> ❗️ This application uses CyberSource's test environment, and should be used with mock data.
>
> All information is secured to our production standards, but
> please use a [test card number](https://developer.cybersource.com/hello-world/testing-guide.html) such as `4111 1111 1111 1111`
> and false name on the `/checkout` page (where information is entered into Microform) to limit usage of any unnecessary personal information.
>
Navigate to http://localhost:8080 and proceed through the various screens
to understand how things work under the hood.


## Tips
- If you are having issues, check out the full [Flex Microform documentation](https://developer.cybersource.com/api/developer-guides/dita-flex/SAFlexibleToken/FlexMicroform.html).
- Safari version 10 and below does not support `RsaOaep256` encryption schema, for those browser please specify encryption type `RsaOaep` when making a call to the `/keys` endpoint.  For a detailed example please see [JwtProcessorService.java](./src/main/java/com/cybersource/example/service/JwtProcessorService.java), line 47.