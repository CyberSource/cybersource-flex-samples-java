/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.application;

import com.cybersource.flex.sdk.FlexService;
import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.model.FlexPublicKey;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    @Autowired
    private FlexService flexService;

    @RequestMapping("/")
    String redirect() {
        return "redirect:checkout";
    }

    @RequestMapping("/checkout")
    String checkout(final HttpSession session, final Model model) throws FlexException {
        // retrieve one time use public RSA key from Flex to facilitate PAN encryption
        final FlexPublicKey key = flexService.createKey();
        session.setAttribute("flexPublicKey", key);

        // Add JSON Web Keystore to the view model and return rendered "checkout" page
        model.addAttribute("jwk", key.getJwk());
        return "checkout";
    }

    @RequestMapping(value = "/receipt", method = RequestMethod.POST)
    String receipt(@RequestParam final Map<String, Object> postParams, final HttpSession session, final Model model) throws FlexException {
        // Read in the public key to be used and remove it from the session
        final FlexPublicKey key = (FlexPublicKey) session.getAttribute("flexPublicKey");
        session.removeAttribute("flexPublicKey"); // no longer needed

        // verify the token signiture using SDK
        postParams.put("verifyResult", flexService.verify(key, postParams));

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
