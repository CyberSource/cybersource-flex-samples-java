/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

package com.cybersource.flex.application;

import com.cybersource.flex.models.KeyParameters;
import com.cybersource.flex.models.KeyResult;
import java.security.PublicKey;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class CheckoutController {

    @Value("${mid}")
    private String mid;
    @Value("${profileId}")
    private String profileId;

    private final static String flexKeysEndpoint = "https://testflex.cybersource.com/cybersource/flex/v1/keys";

    @Autowired
    private SecurityService securityService;
    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/")
    String redirect() {
        return "redirect:checkout";
    }

    @RequestMapping("/checkout")
    String checkout(final HttpSession session, final Model model) {
        // prepare HTTP headers to make server2flex rest call
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-MERCHANT-ID", mid);
        securityService.addSignature(headers);

        // prepare keys endpoint request payload
        KeyParameters requestBody = new KeyParameters();
        requestBody.setProfileId(profileId); // merchant profile
        requestBody.setEncryptionType("WebCryptoAPI"); // encryption type

        // retrieve one time use RSA public key
        HttpEntity<KeyParameters> httpEntity = new HttpEntity<>(requestBody, headers);
        KeyResult key = restTemplate.postForObject(flexKeysEndpoint, httpEntity, KeyResult.class);

        // parse Flex public key in DER format and store it in session for future use. 
        PublicKey flexPublicKey = securityService.decodePublicKey(key.getDer().getPublicKey());
        session.setAttribute("flexPublicKey", flexPublicKey);

        // Add JSON Web Keystore to the view model and return rendered page
        model.addAttribute("jwk", key.getJwk());
        return "checkout";
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.POST)
    String receipt(@RequestParam final Map<String, Object> postParams, final HttpSession session, final Model model) {

        // Read in the public key to use and remove it from the session
        PublicKey flexPublicKey = (PublicKey) session.getAttribute("flexPublicKey");
        session.removeAttribute("flexPublicKey");

        // verify Flex signature passed as POST parameters
        String signedFields = (String) postParams.get("flex_signedFields");
        StringBuilder sb = new StringBuilder();
        for (String k : signedFields.split(",")) {
            sb.append(',');
            sb.append(postParams.get("flex_" + k));
        }
        final String signedValues = sb.substring(1);
        final String signature = (String) postParams.get("flex_signature");
        if (!securityService.verify(flexPublicKey, signedValues, signature)) {
            throw new RuntimeException("The signature is not valid");
        }

        // Add the post params to our view model
        model.addAttribute("postParams", postParams);

        return "receipt";
    }

}