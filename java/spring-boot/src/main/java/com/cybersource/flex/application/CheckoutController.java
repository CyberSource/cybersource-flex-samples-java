/**
 * Copyright (c) 2016 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.application;

import com.cybersource.flex.models.KeyParameters;
import com.cybersource.flex.models.KeyParametersMessageConverter;
import com.cybersource.flex.models.KeyResult;
import com.cybersource.flex.vdp.VDPEnpoints;
import java.security.PublicKey;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class CheckoutController {

    @Value("${vdp.api-key}")
    private String apiKey;
    @Value("${vdp.shared-secret}")
    private String sharedSecret;

    private final FlexSecurityService flexSecurityService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public CheckoutController(final FlexSecurityService flexSecurityService) {
        this.flexSecurityService = flexSecurityService;
    }

    @PostConstruct
    private void postConstruct() {
        restTemplate.getMessageConverters().add(0, new KeyParametersMessageConverter(apiKey, sharedSecret));
    }

    @RequestMapping("/")
    String redirect() {
        return "redirect:checkout";
    }

    @RequestMapping("/checkout")
    String checkout(final HttpSession session, final Model model) {
        // retrieve one time use public RSA key from Flex to facilitate PAN encryption
        final KeyParameters keyParameters = new KeyParameters();
        KeyResult key = restTemplate.postForObject(VDPEnpoints.Sandbox.keysUrl(apiKey), keyParameters, KeyResult.class);

        // parse Flex public key in DER format and store it in session for future use (i.e. to verify token signature)
        PublicKey flexPublicKey = flexSecurityService.decodePublicKey(key.getDer().getPublicKey());
        session.setAttribute("flexPublicKey", flexPublicKey);

        // Add JSON Web Keystore to the view model and return rendered "checkout" page
        model.addAttribute("jwk", key.getJwk());
        return "checkout";
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.POST)
    String receipt(@RequestParam final Map<String, Object> postParams, final HttpSession session, final Model model) {
        // Read in the public key to use and remove it from the session
        PublicKey flexPublicKey = (PublicKey) session.getAttribute("flexPublicKey");
        session.removeAttribute("flexPublicKey"); // no longer needed

        // verify Flex signature passed as POST parameter
        String signedFields = (String) postParams.get("flex_signedFields");
        StringBuilder sb = new StringBuilder();
        for (String k : signedFields.split(",")) {
            sb.append(',');
            sb.append(postParams.get("flex_" + k));
        }
        final String signedValues = sb.substring(1);
        final String signature = (String) postParams.get("flex_signature");
        if (!flexSecurityService.verify(flexPublicKey, signedValues, signature)) {
            throw new RuntimeException("The signature is not valid");
        }

        /**
         *
         * The payment may now be completed using the received & validated
         * token.
         *
         * For demonstration purposes, all post parameters are added to the view
         * model to display data received from cardholder's browser.
         */

        model.addAttribute("postParams", postParams);
        return "receipt";
    }

}
