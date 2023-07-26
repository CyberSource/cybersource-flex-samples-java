/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.forms;

import javax.ws.rs.FormParam;

public class TokenizeForm {
    @FormParam("capture-context")
    private String captureContext;
    @FormParam("encrypted-payload")
    private String encryptedPayload;

    public String getCaptureContext() {
        return captureContext;
    }

    public void setCaptureContext(String captureContext) {
        this.captureContext = captureContext;
    }

    public String getEncryptedPayload() {
        return encryptedPayload;
    }

    public void setEncryptedPayload(String encryptedPayload) {
        this.encryptedPayload = encryptedPayload;
    }
}
