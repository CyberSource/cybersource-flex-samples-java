/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.handlers;

import com.cybersource.samples.forms.RequestCaptureContextForm;
import com.cybersource.samples.services.FlexApiPublicKeysResolver;
import com.cybersource.samples.services.FlexApiService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.Form;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/forms")
public class RequestCaptureContextFormHandler {

    @Inject
    @Location("capture-context.html")
    Template captureContextTemplate;

    @Inject
    @RestClient
    FlexApiService flexApiService;

    @Inject
    FlexApiPublicKeysResolver flexApiPublicKeysResolver;

    @Path("request-capture-context")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance createCaptureContextRequestForm(@Form final RequestCaptureContextForm requestCaptureContextForm) {
        final var payload = requestCaptureContextForm.getCaptureContextRequest();

        try {
            final var session = flexApiService.createSession(payload);
            final var jwtConsumer = new JwtConsumerBuilder()
                    .setVerificationKeyResolver(flexApiPublicKeysResolver)
                    .build();

            final var captureContextClaims = jwtConsumer.processToClaims(session);

            return captureContextTemplate
                    .data("capture-context", session)
                    .data("capture-context-claims", new JsonObject(captureContextClaims.toJson()).encodePrettily());
        } catch (WebApplicationException webApplicationException) {
            throw new RuntimeException("Network error when requesting session from VISA", webApplicationException);
        } catch (InvalidJwtException invalidJwtException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", invalidJwtException); // i.e. JWT has expired
        }
    }

}
