/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.handlers;

import com.cybersource.samples.forms.EncryptDataForm;
import com.cybersource.samples.services.FlexApiPublicKeysResolver;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.annotations.Form;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/forms")
public class EncryptDataFormHandler {

    @Inject
    @Location("jwe.html")
    Template jweTemplate;

    @Inject
    FlexApiPublicKeysResolver flexApiPublicKeysResolver;

    @Path("encrypt-data")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance createCaptureContextRequestForm(@Form final EncryptDataForm encryptDataForm) {
        final JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKeyResolver(flexApiPublicKeysResolver)
                .build();

        try {
            final var captureContextClaims = jwtConsumer.processToClaims(encryptDataForm.getCaptureContext());
            final var flx = captureContextClaims.getClaimValue("flx", Map.class);
            final Map<String, Object> jwk = (Map<String, Object>) flx.get("jwk");
            final var encryptionKey = JsonWebKey.Factory.newJwk(jwk);

            final var plainText = new JsonObject();
            plainText.put("context", encryptDataForm.getCaptureContext());
            plainText.put("data", new JsonObject(encryptDataForm.getData()));
            plainText.put("index", 0);

            JsonWebEncryption encryptedData = new JsonWebEncryption();
            encryptedData.setPlaintext(plainText.encode());
            encryptedData.setKeyIdHeaderValue(encryptionKey.getKeyId());
            encryptedData.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.RSA_OAEP_256);
            encryptedData.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_GCM);
            encryptedData.setKey(encryptionKey.getKey());

            return jweTemplate
                    .data("captureContext", encryptDataForm.getCaptureContext())
                    .data("jwe", encryptedData.getCompactSerialization());
        } catch (InvalidJwtException invalidJwtException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", invalidJwtException); // i.e. JWT is tampered in transit
        } catch (MalformedClaimException malformedClaimException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", malformedClaimException); // i.e. cast operation unsuccessful
        } catch (JoseException joseException) {
            throw new RuntimeException("Error when parsing VISA provided JWT", joseException); // i.e. when parsing one-time use key
        }
    }

}
