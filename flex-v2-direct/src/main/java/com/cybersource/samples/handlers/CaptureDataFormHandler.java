/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.handlers;

import com.cybersource.samples.forms.CaptureDataForm;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.annotations.Form;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Base64;

@Path("/forms")
public class CaptureDataFormHandler {

    @Inject
    @Location("capture-data.html")
    Template captureDataTemplate;

    @Path("capture-data")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance createCaptureContextRequestForm(@Form final CaptureDataForm captureDataForm) {
        return captureDataTemplate
                .data("captureContext", captureDataForm.getCaptureContext())
                .data("data", payloadFromCaptureContext(captureDataForm.getCaptureContext()));
    }

    private String payloadFromCaptureContext(String captureContext) {
        JsonObject payload = payload(captureContext);
        final JsonObject fields = new JsonObject();
        payload = payload.getJsonArray("ctx").getJsonObject(0).getJsonObject("data");
        JsonArray requiredFields = payload.getJsonArray("requiredFields");
        requiredFields.forEach(field -> addFieldToRequest(fields, field.toString()));
        return fields.encodePrettily();
    }

    private void addFieldToRequest(JsonObject fields, String key) {
        String[] split = key.split("\\.");

        for (int i = 0; i < split.length - 1; i++) {
            if (fields.containsKey(split[i])) {
                fields = fields.getJsonObject(split[i]);
            } else {
                fields.put(split[i--], new JsonObject());
            }
        }

        fields.put(split[split.length - 1], "");
    }

    private JsonObject payload(String jwt) {
        // nasty way - do not do this at home
        jwt = jwt.substring(jwt.indexOf('.') + 1);
        jwt = jwt.substring(0, jwt.indexOf('.'));
        jwt = new String(Base64.getDecoder().decode(jwt));
        return new JsonObject(jwt);
    }

}
