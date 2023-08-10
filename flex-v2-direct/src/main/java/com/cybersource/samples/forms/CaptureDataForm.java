/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.forms;

import javax.ws.rs.FormParam;

public class CaptureDataForm {
    @FormParam("capture-context")
    private String captureContext;
    @FormParam("data")
    private String data;

    public String getCaptureContext() {
        return captureContext;
    }

    public void setCaptureContext(String captureContext) {
        this.captureContext = captureContext;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
