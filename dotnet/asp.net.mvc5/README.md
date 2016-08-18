# Flex .NET Example

A minimalist C# ASP.NET example integration using Flex-API tokenization.

## Prerequisites

- [Visual Studio 2015](https://www.visualstudio.com/) - all editions should be able to run the examples.

## Setup Instructions

1. Modify `<appSettings>...</appSettings>` in `./Flex/Web.config` with the credentials provided by customer support in your private BETA invite and that your `.p12` key is placed in the directory specified.

  ```xml
  <appSettings>
    <add key="mid" value="YOUR_MID" />
    <add key="cmmKey" value="123456789012345678901234567890" />
    <add key="organizationId" value="YOUR_ORD_ID" />
    <add key="keyStoreFile" value="C:\your_keystore_file.p12" />
    <add key="keyStorePassword" value="YOUR_KEYSTORE_PASS" />
    <add key="privateKeyPassword" value="YOUR_PRIVKEY_PASS" />    
  </appSettings>
  ```

2. Open `Flex.sln` in Visual studio and launch.
  ```
  Menu > Debug > Start Debugging (F5)
  ```
  This will serve the application from [https://localhost:44319](https://localhost:44319) and should automatically open in your browser.

## Tips

- If you are having issues, checkout the full [FLEX documentation](http://apps.cybersource.com/library/documentation/dev_guides/Secure_Acceptance_Flex/html/).

## Browser Support

- Chrome 37+
- Firefox 34+
- Edge 12+
- Opera 24+

*NB: IE11 and Safari support could be achieved through the use of polyfills for promises and Web Crypto API such as [webcrypto-shim](https://github.com/vibornoff/webcrypto-shim) and [promiz](https://github.com/Zolmeister/promiz). However, these are not included in the examples.*

## Disclaimer

This respository is provided as a learning aid for merchants wishing to integrate with the Flex API.  The code samples are not production ready and are intended for illustrative purposes only. As such, any use of these code samples in a production setting is strongly discouraged. Any usage of these code samples must comply with the license agreement as defined in `LICENSE.md` at the root level of this repository.