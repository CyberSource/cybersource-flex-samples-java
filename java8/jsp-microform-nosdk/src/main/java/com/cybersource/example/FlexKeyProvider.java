/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example;

import java.io.BufferedReader;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public class FlexKeyProvider {

    private static final String HOST = "testflex.cybersource.com";
    private final MerchantCredentials merchantCredentials;

    FlexKeyProvider(InputStream resource) {
        try {
            merchantCredentials = new MerchantCredentials(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String bindFlexKeyToSession(HttpSession session) {
        try {
            final JSONObject request = new JSONObject();
            request.put("encryptionType", "RsaOaep256");
            request.put("targetOrigin", "http://localhost:8080"); // the origin of web page that renders flex microform iframe.
            final String body = request.toString();
            final String date = getServerTime();

            final Map<String, String> signedHeaders = new LinkedHashMap<String, String>();
            signedHeaders.put("host", HOST);
            signedHeaders.put("date", date);
            signedHeaders.put("(request-target)", "post /flex/v1/keys");
            signedHeaders.put("digest", getDigest(body));
            signedHeaders.put("v-c-merchant-id", merchantCredentials.getMerchantId());

            final String signature = generateSignature(signedHeaders, merchantCredentials.getKeyId(), merchantCredentials.getSharedSecret());
            signedHeaders.put("signature", signature);
            signedHeaders.remove("(request-target)");

            String response = post(signedHeaders, body);
            JSONObject flexPublicKey = new JSONObject(response);
            session.setAttribute("flexPublicKey", flexPublicKey);

            return flexPublicKey.getJSONObject("jwk").toString();
        } catch (IOException ioe) {
            throw new RuntimeException("Error receiving Flex public key", ioe);
        }

    }

    public boolean verifyTokenResponse(HttpSession session, String flexResponse) {
        try {
            JSONObject flexPublicKey = (JSONObject) session.getAttribute("flexPublicKey");
            byte[] keyBytes = Base64.decodeBase64(flexPublicKey.getJSONObject("der").getString("publicKey"));
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));

            JSONObject token = new JSONObject(flexResponse);

            String[] signedFieldKeys = token.getString("signedFields").split(",");
            StringBuilder signedValues = new StringBuilder();
            for (String key : signedFieldKeys) {
                signedValues.append(",").append(token.get(key).toString());
            }
            signedValues.deleteCharAt(0);

            final Signature signInstance = Signature.getInstance("SHA512withRSA");
            signInstance.initVerify(publicKey);
            signInstance.update(signedValues.toString().getBytes());
            return signInstance.verify(Base64.decodeBase64(token.getString("signature")));
        } catch (Exception e) {
            return false;
        }
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private static String getDigest(String body) {
        try {
            final MessageDigest digester = MessageDigest.getInstance("SHA-256");
            final byte[] digest = digester.digest(body.getBytes(StandardCharsets.UTF_8));
            return String.format("SHA-256=%s", Base64.encodeBase64String(digest));
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException(nsae); // never thrown unless SHA-256 is not provided.
        }
    }

    public static String generateSignature(Map<String, String> headers, final String keyId, final byte[] sharedSecret) {
        try {
            final Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secretKey = new SecretKeySpec(Base64.decodeBase64(sharedSecret), "HmacSHA256");
            sha256HMAC.init(secretKey);

            final StringBuilder signatureString = new StringBuilder();
            final StringBuilder headersString = new StringBuilder();

            for (Map.Entry<String, String> e : headers.entrySet()) {
                signatureString.append('\n').append(e.getKey()).append(": ").append(e.getValue());
                headersString.append(' ').append(e.getKey());
            }
            signatureString.delete(0, 1);
            headersString.delete(0, 1);

            final StringBuilder signature = new StringBuilder();
            sha256HMAC.update(signatureString.toString().getBytes(StandardCharsets.UTF_8));
            final byte[] hashBytes = sha256HMAC.doFinal();

            signature.append("keyid=\"").append(keyId).append("\", ")
                    .append("algorithm=\"HmacSHA256\", ")
                    .append("headers=\"").append(headersString).append("\", ")
                    .append("signature=\"").append(Base64.encodeBase64String(hashBytes)).append('\"');

            return signature.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String post(Map<String, String> headers, String payload) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://testflex.cybersource.com/flex/v1/keys").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // HEADERS
        connection.setRequestProperty("Accept", "application/json; charset=utf-8");
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("User-Agent", "URLConnection Java JSP GitHub Example");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
        try {
            out.write(payload);
        } finally {
            out.close();
        }

        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        try {
            final char[] buffer = new char[4096];
            int bytesRead;

            while ((bytesRead = reader.read(buffer)) != -1) {
                result.append(buffer, 0, bytesRead);
            }
        } finally {
            reader.close();
        }
        return result.toString();
    }
}
