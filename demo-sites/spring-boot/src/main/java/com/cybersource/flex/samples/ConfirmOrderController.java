/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

package com.cybersource.flex.samples;

import java.security.PublicKey;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmOrderController {

    @Autowired
    private SecurityService securityService;

    @RequestMapping("/confirm-order.html")
    String index(@RequestParam final Map<String, Object> postParams,
            final HttpSession session, final Model model) {
        // verify Flex signature passed as POST parameters
        PublicKey flexPublicKey = (PublicKey) session.getAttribute("flexPublicKey");
        session.removeAttribute("flexPublicKey"); // no more needed
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

        // save payment token for future use, i.e. with Cybersource Payment API
        session.setAttribute("paymentToken", postParams.get("flex_token"));

        session.setAttribute("cardType", postParams.get("flex_cardType"));
        session.setAttribute("cardExpiry", postParams.get("form_card_exp"));
        session.setAttribute("cardCVN2", postParams.get("form_card_cvn2"));
        session.setAttribute("cardMasked", postParams.get("flex_maskedPan"));

        session.setAttribute("customerName", postParams.get("form_name"));
        session.setAttribute("customerEmail", postParams.get("form_email"));
        session.setAttribute("addressHouseNo", postParams.get("form_addr_no"));
        session.setAttribute("addressStreet", postParams.get("form_addr_street"));
        session.setAttribute("addressCity", postParams.get("form_addr_city"));
        session.setAttribute("addressState", postParams.get("form_addr_state"));
        session.setAttribute("addressZip", postParams.get("form_addr_zip"));

        return "confirm-order";
    }
}
