/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example.web;

import Model.GenerateCaptureContextRequest;
import com.cybersource.example.service.JwtProcessorService;
import com.cybersource.example.service.MicroformService;
import com.cybersource.example.service.PaymentsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.nio.file.Files;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"encodedTransientToken", "bootstrapVersion", "captureContextRequest", "clientVersion", "captureContextJwt"})
public class MicroformController {

    private static final String BOOTSTRAP_VERSION = "https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css";

    @Autowired
    private final MicroformService microformService;

    @Autowired
    private final JwtProcessorService jwtProcessorService;

    @Autowired
    private final PaymentsService paymentsService;

    @Value("classpath:capture-context-request.json")
    private Resource captureContextRequestJson;

    @GetMapping("/")
    @SneakyThrows
    public String index(final Model model) {
        // Just setting some variables to render the landing page, nothing Microform-specific here
        try (final Stream<String> lines = Files.lines(captureContextRequestJson.getFile().toPath())) {
            final long lineCount = lines.count();
            model.addAttribute("requestLineCount", lineCount);
        }
        model.addAttribute("requestJson", IOUtils.toString(captureContextRequestJson.getInputStream(), UTF_8));
        model.addAttribute("bootstrapVersion", BOOTSTRAP_VERSION);
        model.addAttribute("captureContextRequest", new GenerateCaptureContextRequest());
        return "index";
    }

    @PostMapping("/capture-context")
    @SneakyThrows
    public String captureContext(final @RequestParam("captureContextRequest") String captureContextRequest, final Model model) {
        // Read the request we got from the frontend
        final GenerateCaptureContextRequest mappedRequest =
                new ObjectMapper().readValue(captureContextRequest.getBytes(UTF_8), GenerateCaptureContextRequest.class);
        // Call the /sessions endpoint to get our capture context back as a JWT
        final String captureContextJwt = microformService.generateCaptureContext(mappedRequest);
        // Verify that it's from CyberSource
        final String decodedBody = jwtProcessorService.verifyJwtAndGetDecodedBody(captureContextJwt);
        // Dynamically retrieve the client library to pass back to our frontend
        final String clientVersion = jwtProcessorService.getClientVersionFromDecodedBody(decodedBody);

        model.addAttribute("captureContextJwt", captureContextJwt);
        model.addAttribute("decodedBody", new ObjectMapper().readTree(decodedBody).toPrettyString());
        model.addAttribute("clientVersion", clientVersion);

        return "capture-context";
    }

    @PostMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @PostMapping("/token")
    @SneakyThrows
    public String token(final @RequestParam("transientToken") String transientTokenJwt, final Model model) {
        // Verify that the transient token came from CyberSource
        final String decodedBody = jwtProcessorService.verifyJwtAndGetDecodedBody(transientTokenJwt);
        model.addAttribute("encodedTransientToken", transientTokenJwt);
        model.addAttribute("decodedTransientToken", new ObjectMapper().readTree(decodedBody).toPrettyString());
        return "token";
    }

    @PostMapping("/receipt")
    @SneakyThrows
    public String receipt(final Model model) {
        // Call the payments endpoint to make a request using our transient token
        final String paymentsResponse =
                paymentsService.makePaymentWithTransientToken(model.getAttribute("encodedTransientToken").toString());
        model.addAttribute("paymentsResponse", paymentsResponse);
        return "receipt";
    }
}
