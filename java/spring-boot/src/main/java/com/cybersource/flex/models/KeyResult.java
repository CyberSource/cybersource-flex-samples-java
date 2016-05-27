/*
 * Copyright 2016 CyberSource Corporation. All rights reserved.
 */
package com.cybersource.flex.models;

public class KeyResult {

    private String keyId;
    private DerPublicKey der;
    private JsonWebKey jwk;

    public String getKeyId() {
        return keyId;
    }
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public DerPublicKey getDer() {
        return der;
    }
    public void setDer(DerPublicKey derPublicKey) {
        this.der = derPublicKey;
    }

    public JsonWebKey getJwk() {
        return jwk;
    }
    public void setJwk(JsonWebKey jsonWebKey) {
        this.jwk = jsonWebKey;
    }

}
