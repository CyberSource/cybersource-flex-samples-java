/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

package com.cybersource.flex.models;

/**
 * This class conforms to the JSON Web Key (JWK) format as
 * defined here: https://tools.ietf.org/html/rfc7517#section-4
 * It's purpose is to allow us to easily serialize out a JWK for
 * client-side use by the WebCryptoApi.
 */
public class JsonWebKey {

    /** Key Type */
    private String kty;
    /** Public Key Use */
    private String use;
    /** Key ID */
    private String kid;
    /** Modulus */
    private String n;
    /** Exponent */
    private String e;

    public String getKty() {
        return kty;
    }
    public void setKty(String keyType) {
        this.kty = keyType;
    }

    public String getUse() { return use; }
    public void setUse(String keyUse) { this.use = keyUse; }

    public String getKid() {
        return kid;
    }
    public void setKid(String keyId) {
        this.kid = keyId;
    }

    public String getN() {
        return n;
    }
    public void setN(String modulus) {
        this.n = modulus;
    }

    public String getE() { return e; }
    public void setE(String exponent) {
        this.e = exponent;
    }
}
