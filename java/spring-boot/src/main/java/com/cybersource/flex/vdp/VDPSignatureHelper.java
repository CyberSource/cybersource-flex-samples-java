/**
 * Copyright (c) 2016 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.vdp;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * VISA Developer Platform helper
 */
public final class VDPSignatureHelper {

    private VDPSignatureHelper() {
        throw new IllegalStateException();
    }

    /**
     * The name of HTTP header carrying the authentication token
     */
    public static final String HTTP_REQHDR_XPAYTOKEN = "X-Pay-Token";

    /**
     * Hashing algorithm used for signing HTTP requests.
     */
    private static final String HASH_ALG = "HmacSHA256";

    /**
     * Generic method to calculate HMAC signature for VDP endpoint.
     *
     * @param resource_path This is the API endpoint you would like to invoke
     * after the context path.
     * @param query_string The API key is a required query parameter. Query
     * parameters should be in lexicographical order.
     * @param request_body This is the API endpoint specific request body
     * string.
     * @param shared_secret The Shared Secret from the application details page.
     * @return
     * @throws VDPSignatureException
     */
    public static String generateXpaytoken(final String resource_path, final String query_string, final String request_body, final String shared_secret) throws VDPSignatureException {
        try {
            final long timestamp = System.currentTimeMillis() / 1000L;
            final Mac sha256HMAC = Mac.getInstance(HASH_ALG);
            final SecretKeySpec secretKey = new SecretKeySpec(shared_secret.getBytes(StandardCharsets.UTF_8), HASH_ALG);
            sha256HMAC.init(secretKey);

            sha256HMAC.update(Long.toString(timestamp).getBytes(StandardCharsets.UTF_8));
            sha256HMAC.update(resource_path.getBytes(StandardCharsets.UTF_8));
            sha256HMAC.update(query_string.getBytes(StandardCharsets.UTF_8));
            sha256HMAC.update(request_body.getBytes(StandardCharsets.UTF_8));

            final byte[] hashByte = sha256HMAC.doFinal();
            return "xv2:" + timestamp + ":" + toHex(hashByte).toLowerCase();
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException e) {
            throw new VDPSignatureException(e);
        }
    }

    /**
     * Converts byte array to hex string representation.
     *
     * @param bytes byte array to be converted to hex string
     * @return hex string representation of byte array
     */
    private static String toHex(byte[] bytes) {
        final BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}
