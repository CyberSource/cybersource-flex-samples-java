/**
 * Copyright (c) 2016 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.models;

import com.cybersource.flex.vdp.VDPSignatureHelper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class KeyParametersMessageConverter extends AbstractHttpMessageConverter<KeyParameters> {

    private final String apiKey;
    private final String sharedSecret;

    public KeyParametersMessageConverter(final String apiKey, final String sharedSecret) {
        super(MediaType.APPLICATION_JSON);
        this.apiKey = apiKey;
        this.sharedSecret = sharedSecret;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return KeyParameters.class.equals(clazz);
    }

    @Override
    protected KeyParameters readInternal(Class<? extends KeyParameters> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Not expected as response.");
    }

    @Override
    protected void writeInternal(KeyParameters keyParameters, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final String body = keyParameters.toString();
        final String xPayToken = VDPSignatureHelper.generateXpaytoken("payments/flex/v1/keys", "apikey=" + apiKey, body, sharedSecret);

        outputMessage.getHeaders().set(VDPSignatureHelper.HTTP_REQHDR_XPAYTOKEN, xPayToken);
        outputMessage.getBody().write(body.getBytes(StandardCharsets.UTF_8));
    }

}
