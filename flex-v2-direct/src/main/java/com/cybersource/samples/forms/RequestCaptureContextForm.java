/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.forms;

import javax.ws.rs.FormParam;

public class RequestCaptureContextForm {
    @FormParam("capture-context-request")
    private String captureContextRequest;

    public String getCaptureContextRequest() {
        return captureContextRequest;
    }

    public void setCaptureContextRequest(String captureContextRequest) {
        this.captureContextRequest = captureContextRequest;
    }
}
