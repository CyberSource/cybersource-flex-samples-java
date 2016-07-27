/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
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
