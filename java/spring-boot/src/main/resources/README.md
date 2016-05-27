# Folder contents

| Folder / Filename      | Purpose        |
|------------------------|----------------|
| /public                | Assets placed here are served publicly from the root of the webserver. In our example this contains all the scripts and css used. |
| /templates             | All [Thymeleaf templates](http://www.thymeleaf.org/) used by the application in rendering html pages. |
| application.properties | The Spring-Boot application configuration. In our example this is used to enable HTTPS via embedded Tomcat as the WebCryptoAPI requires pages to be served via secure protocol only. Additionally, this is where all merchant credentials and configuration is stored (as per setup instructions step 1). |
| embedded-tomcat.jk     | Self signed X.509 certificate keystore generated with keytool command. This certificate is used to mock “merchant site” being served over HTTPS. In a real production environment the SSL certificate should not be part of source code and would typically be provisioned directly to production servers. |
| *.p12                  | Although not included in the source on GitHub, this is the location we recommend placing your `.p12` key. |
| .gitignore             | Prevents any accidental commit of p12 keystore to the git repository. |