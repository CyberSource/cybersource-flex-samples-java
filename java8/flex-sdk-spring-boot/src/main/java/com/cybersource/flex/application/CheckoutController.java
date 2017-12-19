/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.application;

import com.cybersource.flex.sdk.FlexService;
import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.model.FlexPublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    @Autowired
    private FlexService flexService;

    @ModelAttribute
    public void setFramingResponseHeader(HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "DENY");
    }

    @RequestMapping("/")
    String redirect(final HttpSession session) {
        session.invalidate();
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
    String receipt(@RequestParam Map<String, Object> postParams, final HttpSession session, final Model model) throws FlexException {
        postParams = validateUntrustedParameters(postParams);

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

    private Map<String, Object> validateUntrustedParameters(Map<String, Object> parameters) {
        Map<String, Object> retVal = new HashMap<>();
        // Each parameter must undergo proper validation / sanitization.
        // The type of validation to be implemented will vary between individual
        // Flex API integrations. It is merchant's responsibility to implement adequate
        // parameter validation for production deployments.
        parameters.forEach((k, v) -> retVal.put(k, v));
        return retVal;
    }

}
