package com.cybersource.example.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.cybersource.example.config.MerchantCredentials;
import com.cybersource.example.domain.CaptureContextResponseBody;
import com.cybersource.example.domain.CaptureContextResponseHeader;
import com.cybersource.example.domain.JWK;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

@Service
@RequiredArgsConstructor
public class JwtProcessorService {

    @Autowired
    private final MerchantCredentials merchantCredentials;

    @SneakyThrows
    public String verifyJwtAndGetDecodedBody(final String jwt) {
        // Parse the JWT response into header, payload, and signature
        final String[] jwtChunks = jwt.split("\\.");
        final Decoder decoder = Base64.getUrlDecoder();
        final String header = new String(decoder.decode(jwtChunks[0]));
        final String body = new String(decoder.decode(jwtChunks[1]));

        // Normally you'd want to cache the header and JWK, and only hit /flex/v2/public-keys/{kid} when the key rotates.
        // For simplicity and demonstration's sake let's retrieve it every time
        final JWK publicKeyJWK = getPublicKeyFromHeader(header);

        // Construct an RSA Key out of the response we got from the /public-keys endpoint
        final BigInteger modulus = new BigInteger(1, decoder.decode(publicKeyJWK.n()));
        final BigInteger exponent = new BigInteger(1, decoder.decode(publicKeyJWK.e()));
        final RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));

        // Verify the JWT's signature using the public key
        final Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, null);
        final JWTVerifier verifier = JWT.require(algorithm).build();

        // This will throw a runtime exception if there's a signature mismatch.
        verifier.verify(jwt);

        return body;
    }

    @SneakyThrows
    public String getClientVersionFromDecodedBody(final String body) {
        // We've verified the response is from Cybersource, so we can safely pass the client library to our frontend
        final CaptureContextResponseBody mappedCaptureContextResponse =
                new ObjectMapper().readValue(body, CaptureContextResponseBody.class);

        // Dynamically retrieve the client library
        return mappedCaptureContextResponse.ctx().stream().findFirst()
                .map(wrapper -> wrapper.data().clientLibrary())
                .orElseThrow();

    }

    @SneakyThrows
    private JWK getPublicKeyFromHeader(final String jwtHeader) {
        // Again, this process should be cached so you don't need to hit /public-keys
        // You'd want to look for a difference in the header's value (e.g. new key id [kid]) to refresh your cache
        final CaptureContextResponseHeader mappedJwtHeader =
                new ObjectMapper().readValue(jwtHeader, CaptureContextResponseHeader.class);

        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<String> response =
                restTemplate.getForEntity(
                        "https://" + merchantCredentials.getRequestHost() + "/flex/v2/public-keys/" + mappedJwtHeader.kid(),
                        String.class);
        return new ObjectMapper().readValue(response.getBody(), JWK.class);
    }
}
