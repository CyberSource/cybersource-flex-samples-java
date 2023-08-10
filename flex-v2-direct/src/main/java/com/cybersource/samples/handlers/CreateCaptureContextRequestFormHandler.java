/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.handlers;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Path("/forms")
public class CreateCaptureContextRequestFormHandler {

    @Inject
    @Location("create-capture-context-request.html")
    Template createCaptureContextTemplate;

    @Path("create-capture-context-request")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance indexForm(final MultivaluedMap<String, String> parameters) {
        final JsonObject captureContextRequest = new JsonObject();
        final JsonObject fields = new JsonObject();
        captureContextRequest.put("fields", fields);

        parameters.forEach((k, v) -> addFieldToRequest(fields, k, v.get(0)));
        return createCaptureContextTemplate.data("capture_context_request", captureContextRequest.encodePrettily());
    }

    private void addFieldToRequest(JsonObject fields, String key, String value) {
        if ("off".equals(value)) {
            return;
        }
        String[] split = key.split("\\.");

        for (int i = 0; i < split.length; i++) {
            if (fields.containsKey(split[i])) {
                fields = fields.getJsonObject(split[i]);
            } else {
                fields.put(split[i--], new JsonObject());
            }
        }

        if ("required".equals(value)) {
            return;
        }

        fields.put("required", false);
    }

}
