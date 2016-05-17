/*
 * Copyright 2016 CyberSource Corporation. All rights reserved.
 */
package com.cybersource.flex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KeyResult {

    private String keyId;
    @JsonProperty("der")
    private DerPublicKey derPublicKey;
    @JsonProperty("jwk")
    private JsonWebKey jsonWebKey;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public DerPublicKey getDerPublicKey() {
        return derPublicKey;
    }

    public void setDerPublicKey(DerPublicKey derPublicKey) {
        this.derPublicKey = derPublicKey;
    }

    public JsonWebKey getJsonWebKey() {
        return jsonWebKey;
    }

    public void setJsonWebKey(JsonWebKey jsonWebKey) {
        this.jsonWebKey = jsonWebKey;
    }

}
