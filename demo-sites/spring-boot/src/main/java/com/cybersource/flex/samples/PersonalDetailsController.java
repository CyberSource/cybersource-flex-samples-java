/*
 * Copyright 2016 CyberSource Corporation. All rights reserved.
 */
package com.cybersource.flex.samples;

import com.cybersource.flex.model.KeyParameters;
import com.cybersource.flex.model.KeyResult;
import java.security.PublicKey;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class PersonalDetailsController {

    @Value("${mid}")
    private String mid;
    @Value("${profileId}")
    private String profileId;
    @Autowired
    private SecurityService securityService;
    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("/personal-details.html")
    String index(final HttpSession session, final Model model) {
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
        KeyResult key = restTemplate.postForObject(
                "https://testflex.cybersource.com/cybersource/flex/v1/keys",
                httpEntity, KeyResult.class);
        // pass the JSON Web Keystore to templating engine via model
        model.addAttribute("jwk", key.getJsonWebKey().toString());

        // parse Flex public key in DER format and store it in session for future use. 
        PublicKey flexPublicKey = securityService.decodePublicKey(key.getDerPublicKey().getPublicKey());
        session.setAttribute("flexPublicKey", flexPublicKey);

        return "personal-details"; // render personal-details.html
    }

}
