/*
 * Copyright 2016 CyberSource Corporation. All rights reserved.
 */
package com.cybersource.flex.model;

public class DerPublicKey {

    private String format;
    private String algorithm;
    private String publicKey;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
