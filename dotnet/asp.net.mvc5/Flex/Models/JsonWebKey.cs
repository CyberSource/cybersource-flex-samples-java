/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

namespace Flex.Models
{
    /// <summary>
    /// This class conforms to the JSON Web Key (JWK) format as
    /// defined here: https://tools.ietf.org/html/rfc7517#section-4
    /// It's purpose is to allow us to easily serialize out a JWK for
    /// client-side use by the WebCryptoApi.
    /// </summary>
    public class JsonWebKey
    {
        /// <summary>
        /// Key type
        /// </summary>
        public string kty { get; set; }

        /// <summary>
        /// Public Key Use
        /// </summary>
        public string use { get; set; }

        /// <summary>
        /// Key ID
        /// </summary>
        public string kid { get; set; }

        /// <summary>
        /// Modulus
        /// </summary>
        public string n { get; set; }

        /// <summary>
        /// Exponent
        /// </summary>
        public string e { get; set; }
    }
}