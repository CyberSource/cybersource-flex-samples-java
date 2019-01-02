// integration.js
import flex from '@cybersource/flex-sdk-web';

document.querySelector('#payBtn').on('click', () => {
  const options = {
    kid: jwk.kid,
    keystore: jwk,
    cardInfo: {
      cardNumber: document.querySelector('#cardNumber').value,
      cardType: document.querySelector('select[name="cardType"]').value,
      expiryMonth: document.querySelector('input[name="expiryMonth"]').value,
      expiryYear: document.querySelector('input[name="expiryYear"]').value
    },
    // production: true // without specifying this tokens are created in test env
  };

  flex.createToken(options, (response) => {
    if (response.error) {
      alert('There has been an error!');
      console.log(response);
      return;
    }

    document.querySelector("input[name='token']").value = response.token;
    document.querySelector('#paymentForm').submit();
  });
});