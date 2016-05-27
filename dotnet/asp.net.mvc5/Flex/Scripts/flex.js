(function(global) {

  var _flexPublicKey;

  var _endpoints = {
    keys: "https://testflex.cybersource.com/cybersource/flex/v1/keys",
    tokens: "https://testflex.cybersource.com/cybersource/flex/v1/tokens"
  }
  /* PRIVATE FUNCTIONS */
  var _isFunction = function(functionToCheck) {
    var getType = {};
    return functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
  }

  var _arrayBufferToString = function (buf) {
    return String.fromCharCode.apply(null, new Uint8Array(buf));
  }

  var _stringToArrayBuffer = function(str) {
    var buf = new ArrayBuffer(str.length); // 2 bytes for each char
    var bufView = new Uint8Array(buf);
    for (var i=0, strLen=str.length; i < strLen; i++) {
      bufView[i] = str.charCodeAt(i);
    }
    return buf;
  }

  // If webcrypto-shim + promiz libs we will have IE11 & Safari 8+ support
  var _crypto = global.crypto;
  if(_crypto === undefined) {
    throw new Error("Browser does not support WebCryptoApi");
  }

  var _importKey = function(keystore){

    // Remove "use" var due to bug currently in Edge browser
    // https://connect.microsoft.com/IE/feedback/details/2242108/webcryptoapi-importing-jwk-with-use-field-fails
    // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/6202624/
    if (/Edge/.test(global.navigator.userAgent)) {
      delete keystore.use;
    }

    return _crypto.subtle.importKey(
      "jwk", keystore,
      { name: "RSA-OAEP", hash: { name: "SHA-256" }},
      true, ["encrypt"]);
  }

  var _encrypt = function (input, publicKey){
    if(!publicKey){ throw new Error("Public key has not been imported."); }
    return _crypto.subtle.encrypt(
      {
        name: "RSA-OAEP",
        // this is out of spec but required for edge
        // http://answers.microsoft.com/en-us/windows/forum/apps_windows_10-msedge/rsa-public-key-encryption-in-edge/c6634469-283c-46ce-a4d8-357baaab267f
        hash: { name: "SHA-256" }
      },
      publicKey,
      _stringToArrayBuffer(input)
    );
  }

  var _tokenise = function(options, responseHandler, errorHandler){
    if(_flexPublicKey === undefined) {
      throw new Error("FLEX.setup has not been called."); 
    }

    /* Check our response handlers are functions */
    if(responseHandler === undefined || !_isFunction(responseHandler)) {
      throw new Error("responseHandler is not a function");
    }
    if(errorHandler === undefined || !_isFunction(errorHandler)) {
      throw new Error("errorHandler is not a function");
    }

    _encrypt(options.cardNumber, _flexPublicKey).then(function(encryptedCardNumber) {

      var request = new XMLHttpRequest();
      request.open('POST', _endpoints.tokens, true);
      request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

      /* Response handler */
      request.onload = function(){
      if (request.readyState === 4 && request.status >= 200 && request.status < 400) {
        responseHandler(request.status, JSON.parse(request.responseText));
      }
      else {
        errorHandler(request.status, request.responseText);
      }
    }

    /* Catch all for connectivity issues */
    request.onerror = function() {
      errorHandler(request.status, request.responseText);
    };

    var ciphertext = new Uint8Array(encryptedCardNumber);
    var base64Encoded = global.btoa(String.fromCharCode.apply(null, ciphertext));

    var tokenisationRequestData = {
      keyId: global.keystore['kid'],
      cardDetails: {
        cardNumber: base64Encoded,
        cardType: options.cardType
      }
    };

    console.info("Tokenising...");
    request.send(JSON.stringify(tokenisationRequestData));
   })
   .catch(function(err){
     console.log(err);
      console.error("There was an error during tokenisation");
    });
  }

  var _checkBrowserSupport = function(){

    /* Web Crypto API */
    if(_crypto === undefined || _crypto.subtle === undefined || !_isFunction(_crypto.subtle.encrypt)){
      throw new Error("Your browser does not support Web Crypto API.");
    }

    /* btoa */
    if(global.btoa === undefined || !_isFunction(global.btoa)){
      throw new Error("Your browser does not support btoa.");
    }
  }

  var _setup = function(keystore, options){
    /* This will throw if the browser doesn't support the required functions
     * in future browser support will be much broader with graceful fallback
     * through polyfils etc.
     **/
    _checkBrowserSupport();

    var importKey = _importKey(keystore)
      .then(function (importedKey) { _flexPublicKey = importedKey; })
      .catch(function (err) { console.error(err); });
  }

  /* PUBLIC FUNCTIONS */
  var _FLEX = {
    setup: _setup,
    createToken: _tokenise
  };

  /* Expose FLEX as a global */
  global.FLEX = _FLEX;
  console.info("FLEX:  SDK loaded");

}(this))