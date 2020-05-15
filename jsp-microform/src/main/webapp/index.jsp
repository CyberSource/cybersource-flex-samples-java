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
            #number-container, #securityCode-container {
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

   <div class="container card">
            <div class="card-body">
                <h1>Checkout</h1>
                <div id="errors-output" role="alert"></div>
                <form action="/token" id="my-sample-form" method="post">
                    <div class="form-group">
                        <label for="cardholderName">Name</label>
                        <input id="cardholderName" class="form-control" name="cardholderName" placeholder="Name on the card">
                        <label id="cardNumber-label">Card Number</label>
                        <div id="number-container" class="form-control"></div>
                        <label for="securityCode-container">Security Code</label>
                        <div id="securityCode-container" class="form-control"></div>
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
                                <option>2021</option>
                                <option>2022</option>
                                <option>2023</option>
                            </select>
                        </div>
                    </div>

                    <button type="button" id="pay-button" class="btn btn-primary">Pay</button>
                    <input type="hidden" id="flexresponse" name="flexresponse">
                </form>
            </div>
        </div>

    <script src="https://flex.cybersource.com/cybersource/assets/microform/0.11/flex-microform.min.js"></script>
    
            
  <script>
            // JWK is set up on the server side route for /

            var form = document.querySelector('#my-sample-form');
            var payButton = document.querySelector('#pay-button');
            var flexResponse = document.querySelector('#flexresponse');
            var expMonth = document.querySelector('#expMonth');
            var expYear = document.querySelector('#expYear');
            var errorsOutput = document.querySelector('#errors-output');
          

            var captureContext = '<%= ((FlexKeyProvider) request.getServletContext().getAttribute(FlexKeyProvider.class.getName()))
                    .bindFlexKeyToSession(session) %>';

             // custom styles that will be applied to each field we create using Microform
            var myStyles = {  
              'input': {    
                'font-size': '14px',    
                'font-family': 'helvetica, tahoma, calibri, sans-serif',    
                'color': '#555'  
              },  
              ':focus': { 'color': 'blue' },  
              ':disabled': { 'cursor': 'not-allowed' },  
              'valid': { 'color': '#3c763d' },  
              'invalid': { 'color': '#a94442' }
            };

            console.log(captureContext);

            // setup
            var flex = new Flex(captureContext);
            var microform = flex.microform({ styles: myStyles });
            var number = microform.createField('number', { placeholder: 'Enter card number' });
            var securityCode = microform.createField('securityCode', { placeholder: '•••' });

            number.load('#number-container');
            securityCode.load('#securityCode-container');


            payButton.addEventListener('click', function() {  
              var options = {    
                cardExpirationMonth: expMonth.value,  
                cardExpirationYear: expYear.value 
              };

              microform.createToken(options, function (err, token) {
                if (err) {
                  // handle error
                  console.error(err);
                  errorsOutput.textContent = err.message;
                } else {
                  // At this point you may pass the token back to your server as you wish.
                  // In this example we append a hidden input to the form and submit it.      
                  console.log(JSON.stringify(token));
                  flexResponse.value = JSON.stringify(token);
                  form.submit();
                }
              });
            });
        </script>
    </body>
</html>