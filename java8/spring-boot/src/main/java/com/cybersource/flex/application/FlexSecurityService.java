/**
 * Copyright (c) 2016 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.application;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class FlexSecurityService {

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public PublicKey decodePublicKey(String derEncodedKey) {
        try {
            byte[] keyBytes = DECODER.decode(derEncodedKey);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
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
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            throw new RuntimeException(ex);
        }
    }

}
