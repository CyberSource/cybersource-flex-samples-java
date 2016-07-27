/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

function tokenise(options, $form){
	// Encrypt the card number
	crypto.subtle.encrypt({name: "RSA-OAEP"}, flexPublicKey, stringToArrayBuffer(options.cardNumber)).then(function(encryptedCardnumber){

		var ciphertext = new Uint8Array(encryptedCardnumber);
		var base64Encoded = window.btoa(String.fromCharCode.apply(null, ciphertext));

		var dataToSend = {
			keyId: window.keystore['kid'],
            cardInfo: {
              cardNumber: base64Encoded, // Base64 encode the encrypted PAN
              cardType: options.cardType
            }
		};

		// Add optional fields
        if(options.cardExpirationMonth) { dataToSend['cardInfo']['cardExpirationMonth'] = options.cardExpirationMonth; }
        if(options.cardExpirationYear)  { dataToSend['cardInfo']['cardExpirationYear'] = options.cardExpirationYear; }

		// Ajax request to the tokens endpoint
		$.ajax({
			type: "POST",
			url: "https://testflex.cybersource.com/cybersource/flex/v1/tokens",
			contentType: 'application/json',
			data: JSON.stringify(dataToSend),
			success: function(response){
				// Append response to form as hidden field and submit to merchant endpoint
				$form.append(createHiddenFields(response)).submit();
			},
			error: function(e){
				alert("There has been an error");
				console.log(e);
				$('#sidebar-confirm-btn, #footer-confirm-btn').prop("disabled", false);
			}
		});
	});	
}

function createHiddenFields(response){
	// fields not to be injected
	delete response.discoverableServices;

  // Create a hidden inputs for the tokenised response
  var wrapper = $("<div>");
  for (var key in response) {
    $("<input type='hidden'>").attr('name', 'flex_'+key).val(response[key]).appendTo(wrapper);
  }

  return wrapper;
}

function stringToArrayBuffer(str) {
  var buf = new ArrayBuffer(str.length);
  var bufView = new Uint8Array(buf);
  for (var i = 0, strLen = str.length; i < strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
}

// Doc ready
$(document).ready(function(){

  // Import the keystore
  var importKey = window.crypto.subtle.importKey("jwk", keystore, {	name: "RSA-OAEP",	hash: {name: "SHA-256"} }, true,["encrypt"])
    .then(function (k) {	window.flexPublicKey = k; })
	.catch(function (err) { console.error(err); });

	// Pay button handlers
	$('#sidebar-confirm-btn, #footer-confirm-btn').on("click", function(){		
	  $('#sidebar-confirm-btn, #footer-confirm-btn').prop("disabled", true);

	    var cardData = {
          cardType: '001',
          cardNumber: $('#pan').val()

          // OMITTED FROM EXAMPLE
          //cardExpirationMonth: ,
          //cardExpirationYear:
        }
	    tokenise(cardData, $("#flex-form"));
    });
});