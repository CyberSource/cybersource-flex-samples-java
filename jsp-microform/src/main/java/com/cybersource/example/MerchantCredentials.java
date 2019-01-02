/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

public class MerchantCredentials {

    private final Map<String, char[]> properties;

    public MerchantCredentials(InputStream resource) throws IOException {
        properties = new CharArrayProperties(resource);
    }

    public String getMerchantId() {
        char[] merchantId = properties.get("merchantId");
        return (merchantId != null) ? new String(merchantId) : null;
    }

    public String getKeyId() {
        char[] keyId = properties.get("keyId");
        return (keyId != null) ? new String(keyId) : null;
    }

    public char[] getSharedSecret() {
        return properties.get("sharedSecret");
    }

    public void destroy() {
        for (char[] value : properties.values()) {
            Arrays.fill(value, '\0');
        }
    }
}
