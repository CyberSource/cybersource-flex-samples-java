/**
 * Copyright (c) 2021 by CyberSource
 */
package com.cybersource.samples.services;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/flex/v2")
@RegisterRestClient(baseUri = "https://apitest.cybersource.com")
public interface FlexApiService {

    @GET
    @Path("/public-keys/{kid}")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> publicKey(@PathParam("kid") String kid);

    @POST
    @Path("/sessions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/jwt")
    String createSession(String request);

    @POST
    @Path("/tokens")
    @Consumes("application/jwt")
    @Produces("application/jwt")
    String tokenize(String jwe);

}
