This folder contains a number of files

.gitignore – This file prevents any accidental commit of p12 keystore to git repository.
application.properties – The Spring-Boot application configuration. It is used mainly to enable HTTPS via embedded Tomcat. It is required that pages that uses WebCryptoAPI are served via secure protocol only.
embedded-tomcat.jks – Self signed X.509 certificate keystore generated with keytool command. This certificate is used to mock “merchant site” being served over HTTPS. In real production environment the SSL certificate shall not be part of source code and are usually provisioned directly to production servers.

