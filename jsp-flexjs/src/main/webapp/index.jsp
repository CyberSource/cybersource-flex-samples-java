<%@page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cybersource.example.FlexKeyProvider"%>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">

    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Checkout</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous" />
        <link rel="stylesheet" href="site.css" />
    </head>

    <body>
        <div class="container wrapper">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <span class="glyphicon glyphicon-lock"></span> Payment Form
                </div>
                <div class="panel-body">
                    <form accept-charset="UTF-8" action="receipt.jsp" autocomplete="off" method="post" novalidate="novalidate" id="payment-form">
                        <div class="form-group">
                            <label for="cardType">Card Type</label>
                            <select class="form-control" id="cardType" name="cardType">
                                <option value="001">VISA</option>
                                <option value="002">MASTERCARD</option>
                                <option value="003">AMEX</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="cardNumber">Card number</label>
                            <input type="text" id="cardNumber" class="form-control" maxlength="19" />
                        </div>
                        <div class="form-group">
                            <label for="cvn">Security code</label>
                            <input type="text" id="cvn" name="cvn" class="form-control" maxlength="4" />
                        </div>
                        <div class="form-group">
                            <label for="cardExpirationMonth">Expiration Date</label>
                            <div class="row">
                                <div class="col-xs-6">
                                    <select class="form-control" name="expiryMonth" id="expiryMonth">
                                        <option value="01">01 (JAN)</option>
                                        <option value="02">02 (FEB)</option>
                                        <option value="03">03 (MAR)</option>
                                        <option value="04">04 (APR)</option>
                                        <option value="05">05 (MAY)</option>
                                        <option value="06">06 (JUN)</option>
                                        <option value="07">07 (JUL)</option>
                                        <option value="08">08 (AUG)</option>
                                        <option value="09">09 (SEP)</option>
                                        <option value="10">10 (OCT)</option>
                                        <option value="11">11 (NOV)</option>
                                        <option value="12">12 (DEC)</option>
                                    </select>
                                </div>
                                <div class="col-xs-6">
                                    <select class="form-control" name="expiryYear" id="expiryYear">
                                        <option>2019</option>
                                        <option>2020</option>
                                        <option>2021</option>
                                        <option>2022</option>
                                        <option>2023</option>
                                        <option>2024</option>
                                        <option>2025</option>
                                        <option>2026</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <!--HIDDEN INPUT FOR TOKENISED RESPONSE-->
                        <input type="hidden" id="flex-response" name="flex-response">

                        <!-- Button to attach our tokenise handler to. Note that it is NOT a submission input or button -->
                        <button type="button" id="pay-btn" class="btn btn-lg btn-block btn-primary">Pay Now</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- For production usage we recommend retrieving and validating the SDK from a validated source to your own servers -->
        <script src="https://cdn.jsdelivr.net/npm/@cybersource/flex-sdk-web"></script>
        <script>
            console.log(window.FLEX.version);

            var jwk = <%=((FlexKeyProvider) request.getServletContext().getAttribute(FlexKeyProvider.class.getName()))
                                .bindFlexKeyToSession(session)%>;

            var payButton = document.querySelector('#pay-btn');
            var flexResponse = document.querySelector('#flex-response');


            var responseHandler = function (response) {
                if (response.error) {
                    alert('There has been an error!');
                    console.log(response);

                    payButton.disabled = false;
                    payButton.innerHTML = 'Pay Now';
                    return;
                }

                console.log('Token generated: ');
                console.log(JSON.stringify(response));

                // At this point the token may be added to the form
                // as hidden fields and the submission continued
                flexResponse.value = JSON.stringify(response);
                document.querySelector('#payment-form').submit();

            }

            payButton.onclick = function () {
                payButton.disabled = true;
                payButton.innerHTML = "Processing...";

                var options = {
                    kid: jwk.kid,
                    keystore: jwk,
                    cardInfo: {
                        cardNumber: document.querySelector('#cardNumber').value,
                        cardType: document.querySelector('#cardType').value,
                        expiryMonth: document.querySelector('#expiryMonth').value,
                        expiryYear: document.querySelector('#expiryYear').value
                    },
                    encryptionType: 'rsaoaep256'
                    // production: true // without specifying this tokens are created in test env
                };

                FLEX.createToken(options, responseHandler);
            };
         </script>
    </body>
</html>
