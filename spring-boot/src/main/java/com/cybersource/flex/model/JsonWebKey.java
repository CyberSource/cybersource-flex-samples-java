/*
 * Copyright 2016 CyberSource Corporation. All rights reserved.
 */
package com.cybersource.flex.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonWebKey {

    @JsonProperty("kty")
    private String keyType;
    @JsonProperty("use")
    private String keyUse;
    @JsonProperty("kid")
    private String keyId;
    @JsonProperty("n")
    private String modulus;
    @JsonProperty("e")
    private String exponent;

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyUse() {
        return keyUse;
    }

    public void setKeyUse(String keyUse) {
        this.keyUse = keyUse;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getModulus() {
        return modulus;
    }

    public void setModulus(String modulus) {
        this.modulus = modulus;
    }

    public String getExponent() {
        return exponent;
    }

    public void setExponent(String exponent) {
        this.exponent = exponent;
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("    \"kty\": \"").append(keyType).append("\",\n");
        json.append("    \"use\": \"").append(keyUse).append("\",\n");
        json.append("    \"kid\": \"").append(keyId).append("\",\n");
        json.append("    \"n\": \"").append(modulus).append("\",\n");
        json.append("    \"e\": \"").append(exponent).append("\"\n");
        json.append("}");
        return json.toString();
    }

}
