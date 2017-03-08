/**
 * Copyright (c) 2016 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.vdp;

public enum VDPEnpoints {
    Sandbox("https://sandbox.api.visa.com/cybersource/payments/flex/v1/keys", "https://sandbox.webapi.visa.com/cybersource/payments/flex/v1/tokens"),
    Production("https://api.visa.com/cybersource/payments/flex/v1/keys", "https://api.visa.com/cybersource/payments/flex/v1/tokens");

    private final String keysEnpoint;
    private final String tokensEnpoint;

    private VDPEnpoints(String keysEnpoint, String tokensEnpoint) {
        this.keysEnpoint = keysEnpoint;
        this.tokensEnpoint = tokensEnpoint;
    }

    public String getKeysEnpoint() {
        return keysEnpoint;
    }

    public String getTokensEnpoint() {
        return tokensEnpoint;
    }

    public String keysUrl(final String apiKey) {
        return String.format("%s?apikey=%s", keysEnpoint, apiKey);
    }

}
