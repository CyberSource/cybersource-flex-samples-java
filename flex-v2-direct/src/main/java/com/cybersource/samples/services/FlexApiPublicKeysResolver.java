/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.jose4j.lang.UnresolvableKeyException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class FlexApiPublicKeysResolver implements VerificationKeyResolver {

    private final Map<String, PublicJsonWebKey> cache = new ConcurrentHashMap<>();

    @Inject
    @RestClient
    FlexApiService flexApiService;

    private PublicJsonWebKey getPublicKey(String kid) throws UnresolvableKeyException {
        if (cache.containsKey(kid))
            return cache.get(kid);

        try {
            final Map<String, Object> publicKey = flexApiService.publicKey(kid);
            final RsaJsonWebKey rsaJsonWebKey = new RsaJsonWebKey(publicKey);
            cache.put(kid, rsaJsonWebKey);
            return rsaJsonWebKey;
        } catch (WebApplicationException webApplicationException) {
            throw new UnresolvableKeyException("Network error when retrieving Flex API key from VISA", webApplicationException);
        } catch (JoseException joseException) {
            throw new UnresolvableKeyException("Unable to parse public key value retrieved from VISA", joseException);
        }
    }

    @Override
    public Key resolveKey(JsonWebSignature jws, List<JsonWebStructure> nestingContext) throws UnresolvableKeyException {
        final var publicKey = getPublicKey(jws.getKeyIdHeaderValue());
        return publicKey.getKey();
    }
}
