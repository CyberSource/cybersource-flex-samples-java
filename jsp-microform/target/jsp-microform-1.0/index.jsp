<%@page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cybersource.example.FlexKeyProvider"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Sample Checkout</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">

        <style>
            #cardNumber-container {
                height: 38px;
            }

            .flex-microform-focused {
                background-color: #fff;
                border-color: #80bdff;
                outline: 0;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
            }
        </style>
    </head>

    <%
        session.invalidate();
        session = request.getSession(true);
    %>

    <body>
        <div class="container card">
            <div class="card-body">
                <h1>Checkout</h1>
                <form action="receipt.jsp" id="my-sample-form" method="post">
                    <div class="form-group">
                        <label for="cardholderName">Name</label>
                        <input id="cardholderName" class="form-control" name="cardholderName" placeholder="Name on the card">
                    </div>
                    <div class="form-group">
                        <label id="cardNumber-label">Card Number</label>
                        <div id="cardNumber-container" class="form-control"></div>
                    </div>

                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <label for="expMonth">Expiry month</label>
                            <select id="expMonth" class="form-control">
                                <option>01</option>
                                <option>02</option>
                                <option>03</option>
                                <option>04</option>
                                <option>05</option>
                                <option>06</option>
                                <option>07</option>
                                <option>08</option>
                                <option>09</option>
                                <option>10</option>
                                <option>11</option>
                                <option>12</option>
                            </select>
                        </div>
                        <div class="form-group col-md-6">
                            <label for="expYear">Expiry year</label>
                            <select id="expYear" class="form-control">
                                <option>2019</option>
                                <option>2020</option>
                                <option>2021</option>
                            </select>
                        </div>
                    </div>

                    <button type="button" id="pay-button" class="btn btn-primary">Pay</button>
                    <input type="hidden" id="flex-response" name="flex-response">
                </form>
            </div>
        </div>

        <script src="https://testflex.cybersource.com/cybersource/assets/microform/0.2.0/flex-microform.min.js"></script>
        <script>
            var jwk = <%=((FlexKeyProvider) request.getServletContext().getAttribute(FlexKeyProvider.class.getName()))
                    .bindFlexKeyToSession(session)%>;

            var form = document.querySelector('#my-sample-form');
            var payButton = document.querySelector('#pay-button');
            var flexResponse = document.querySelector('#flex-response');
            var expMonth = document.querySelector('#expMonth');
            var expYear = document.querySelector('#expYear');

            // SETUP MICROFORM
            FLEX.microform(
                    {
                        keyId: jwk.kid,
                        keystore: jwk,
                        container: '#cardNumber-container',
                        label: '#cardNumber-label',
                        placeholder: 'Enter Card Number here',
                        styles: {
                            'input': {
                                'font-family': '-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif,"Apple Color Emoji","Segoe UI Emoji","Segoe UI Symbol"',
                                'font-size': '1rem',
                                'line-height': '1.5',
                                'color': '#495057',
                            },
                            ':focus': {'color': 'blue'},
                            ':disabled': {'cursor': 'not-allowed'},
                            'valid': {'color': '#3c763d'},
                            'invalid': {'color': '#a94442'},
                        },
                        encryptionType: 'rsaoaep256'
                    },
                    function (setupError, microformInstance) {
                        if (setupError) {
                            // handle error
                            return;
                        }

                        // intercept the form submission and make a tokenize request instead
                        payButton.addEventListener('click', function () {

                            // Send in optional parameters from other parts of your payment form
                            var options = {
                                cardExpirationMonth: expMonth.value,
                                cardExpirationYear: expYear.value
                                        // cardType: /* ... */
                            };

                            microformInstance.createToken(options, function (err, response) {
                                if (err) {
                                    alert(err);
                                    return;
                                }

                                console.log('Token generated: ');
                                console.log(JSON.stringify(response));

                                // At this point the token may be added to the form
                                // as hidden fields and the submission continued
                                flexResponse.value = JSON.stringify(response);
                                form.submit();
                            });
                        });

                    }
            );
        </script>
    </body>
</html>