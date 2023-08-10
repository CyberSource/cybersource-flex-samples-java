/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.services;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Provider
@ConstrainedTo(RuntimeType.CLIENT)
public class FlexApiHeaderAuthenticator implements WriterInterceptor {

    /**
     * Cybersource test credentials - please replace with your CyberSource REST credentials created through EBC Portal.
     */
    private final String mid = "";
    private final String kid = "";
    private final String secret = "";

    private final String host = "apitest.cybersource.com";

    private static final String HTTP_REQHDR_CONTENTYPE = "content-type";
    /**
     * The name of HTTP header carrying the Merchant ID
     */
    private static final String HTTP_REQHDR_MIDHEADER = "v-c-merchant-id";
    /**
     * The name of HTTP header carrying the current timestamp, please see
     * https://tools.ietf.org/html/rfc7231#section-7.1.1.1
     */
    private static final String HTTP_REQHDR_DATE = "date";
    /**
     * The name of HTTP header carrying the host part of URL
     */
    private static final String HTTP_REQHDR_HOST = "host";
    /**
     * The name of HTTP header carrying the http verb and path. Please see
     */
    private static final String HTTP_REQHDR_REQUEST_TARGET = "(request-target)";
    /**
     * The name of HTTP header carrying the body digest. Please see RFC 3230
     */
    private static final String HTTP_REQHDR_DIGEST = "digest";
    /**
     * The name of HTTP header carrying the body digest. Please see RFC 7230 and
     * RFC 7540
     */
    private static final String HTTP_REQHDR_SIGNATURE = "signature";
    /**
     * Hashing algorithm used for signing HTTP requests.
     */
    private static final String HMAC_ALG = "HmacSHA256";

    private static String getDigest(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(payload.getBytes(StandardCharsets.UTF_8));
            String shaValue = Base64.getEncoder().encodeToString(digest);
            return "SHA-256=" + shaValue;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private static String sign(Map<String, String> headers, final String keyId, final SecretKeySpec secretKey) {
        try {
            final Mac sha256HMAC = Mac.getInstance(HMAC_ALG);
            sha256HMAC.init(secretKey);

            final StringBuilder signatureString = new StringBuilder();
            final StringBuilder headersString = new StringBuilder();

            for (Map.Entry<String, String> e : headers.entrySet()) {
                signatureString.append('\n').append(e.getKey().toLowerCase()).append(": ").append(e.getValue());
                headersString.append(' ').append(e.getKey().toLowerCase());
            }
            signatureString.delete(0, 1);
            headersString.delete(0, 1);

            final StringBuilder signature = new StringBuilder();
            sha256HMAC.update(signatureString.toString().getBytes(StandardCharsets.UTF_8));
            final byte[] hashBytes = sha256HMAC.doFinal();

            signature.append("keyid=\"").append(keyId).append("\", ")
                    .append("algorithm=\"HmacSHA256\", ")
                    .append("headers=\"").append(headersString).append("\", ")
                    .append("signature=\"").append(Base64.getEncoder().encodeToString(hashBytes)).append('\"');

            return signature.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final Method clientMethod = (Method) writerInterceptorContext.getProperty("org.eclipse.microprofile.rest.client.invokedMethod");

        if ("createSession".equals(clientMethod.getName())) {
            final var headers = writerInterceptorContext.getHeaders();
            addAuthHeaders(headers, "post /flex/v2/sessions", writerInterceptorContext.getEntity().toString());
        }

        writerInterceptorContext.proceed();
    }

    private void addAuthHeaders(
            final MultivaluedMap<String, Object> headers,
            final String requestTarget,
            final String payload) {
        final Map<String, String> signedHeaders = new HashMap<>();
        final var date = getServerTime();
        final var digest = getDigest(payload);

        signedHeaders.put(HTTP_REQHDR_HOST, host);
        signedHeaders.put(HTTP_REQHDR_DATE, date);
        signedHeaders.put(HTTP_REQHDR_REQUEST_TARGET, requestTarget);
        signedHeaders.put(HTTP_REQHDR_DIGEST, digest);
        signedHeaders.put(HTTP_REQHDR_MIDHEADER, mid);
        signedHeaders.put(HTTP_REQHDR_CONTENTYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE).toString());

        final var secretKey = new SecretKeySpec(Base64.getDecoder().decode(secret), HMAC_ALG);
        final var signature = sign(signedHeaders, kid, secretKey);

        headers.add(HttpHeaders.DATE, date);
        headers.add("digest", digest);
        headers.add("signature", signature);
        headers.add("v-c-merchant-id", mid);
    }

}