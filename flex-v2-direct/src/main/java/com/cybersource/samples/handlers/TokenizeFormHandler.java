/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.handlers;

import com.cybersource.samples.forms.TokenizeForm;
import com.cybersource.samples.services.FlexApiPublicKeysResolver;
import com.cybersource.samples.services.FlexApiService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.Form;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/forms")
public class TokenizeFormHandler {

    @Inject
    @Location("transient-token.html")
    Template transientTokenTemplate;

    @Inject
    @RestClient
    FlexApiService flexApiService;

    @Inject
    FlexApiPublicKeysResolver flexApiPublicKeysResolver;

    @Path("tokenize")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance createCaptureContextRequestForm(@Form final TokenizeForm tokenizeForm) {
        final var ccConsumer = new JwtConsumerBuilder()
                .setVerificationKeyResolver(flexApiPublicKeysResolver)
                .build();

        try {
            final var transientToken = flexApiService.tokenize(tokenizeForm.getEncryptedPayload());

            final var captureContextClaims = ccConsumer.processToClaims(tokenizeForm.getCaptureContext());
            final var flx = captureContextClaims.getClaimValue("flx", Map.class);
            final Map<String, Object> jwk = (Map<String, Object>) flx.get("jwk");
            final var ttValidationKey = JsonWebKey.Factory.newJwk(jwk);

            final var ttConsumer = new JwtConsumerBuilder()
                    .setVerificationKey(ttValidationKey.getKey())
                    .build();

            final var transientTokenClaims = ttConsumer.processToClaims(transientToken);

            return transientTokenTemplate
                    .data("transientToken", transientToken)
                    .data("claims", new JsonObject(transientTokenClaims.toJson()).encodePrettily());
        } catch (WebApplicationException webApplicationException) {
            throw new RuntimeException("Error when trying to tokenize data.", webApplicationException); // Network error when tokenizing
        } catch (InvalidJwtException invalidJwtException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", invalidJwtException); // i.e. JWT (cc) is tampered in transit
        } catch (MalformedClaimException malformedClaimException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", malformedClaimException); // i.e. cast operation unsuccessful
        } catch (JoseException joseException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", joseException); // i.e. when parsing one-time use key
        }
    }

}
