/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

package com.cybersource.flex.samples;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    @Value("${cmmKey}")
    private String cmmKey;
    @Value("${organizationId}")
    private String organizationId;
    @Value("${keyStoreFile}")
    private String keyStoreFile;
    @Value("${keyStorePassword}")
    private String keyStorePassword;
    @Value("${privateKeyPassword}")
    private String privateKeyPassword;

    public SecurityService() {
        // Add Bouncy Castle JCE provider dynamically.
        // http://www.bouncycastle.org/wiki/display/JA1/Provider+Installation
        Security.addProvider(new BouncyCastleProvider());
        // Note: to run this example you *must* install the unlimited policy files in the JVM you are using.
    }

    /**
     * This method adds HTTP authentication headers.
     * 
     * @param headers map to be added to
     */
    public void addSignature(HttpHeaders headers) {
        try {
            // data used to generate signature
            final long time = System.currentTimeMillis(); // current timestamp
            final String contentType = "application/json";
            final String httpVerb = "post";

            // Retrieving RSA private key from P12 keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12", BouncyCastleProvider.PROVIDER_NAME);
            ClassPathResource keyResource = new ClassPathResource(keyStoreFile);
            keyStore.load(keyResource.getInputStream(), keyStorePassword.toCharArray());
            PrivateKey privateKey = (PrivateKey) keyStore.getKey("serialNumber=" + cmmKey + ",CN=" + organizationId, privateKeyPassword.toCharArray());

            // Preparing data string that will be used to derive the signature
            StringBuilder dataToSign = new StringBuilder();
            dataToSign.append("cmm-key=").append(cmmKey).append(',');
            dataToSign.append("content-type=").append(contentType).append(',');
            dataToSign.append("http-verb=").append(httpVerb).append(',');
            dataToSign.append("organization-id=").append(organizationId).append(',');
            dataToSign.append("time=").append(time);

            // signing data
            Signature signature = Signature.getInstance("SHA512withRSA", BouncyCastleProvider.PROVIDER_NAME);
            signature.initSign(privateKey);
            signature.update(dataToSign.toString().getBytes());
            byte[] signed = signature.sign();
            String encoded = ENCODER.encodeToString(signed); // transform byte array to Base64 string

            // populating signature data to HTTP headers
            headers.set("Authorization", "CWS CWSAccessKeyId:" + encoded);
            headers.set("HTTP-Verb", httpVerb);
            headers.set("Organization-Id", organizationId);
            headers.set("Time", "" + time); // timestamp used to generate signature
            headers.set("CMM-Key", cmmKey);
            headers.set("Content-Type", contentType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey decodePublicKey(String derEncodedKey) {
        try {
            byte[] keyBytes = DECODER.decode(derEncodedKey);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            return publicKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(final PublicKey publicKey, final String dataToVerify, final String base64Signature) {
        try {
            final Signature signInstance = Signature.getInstance("SHA512withRSA");
            signInstance.initVerify(publicKey);
            signInstance.update(dataToVerify.getBytes());
            byte[] signature = DECODER.decode(base64Signature);
            return signInstance.verify(signature);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
