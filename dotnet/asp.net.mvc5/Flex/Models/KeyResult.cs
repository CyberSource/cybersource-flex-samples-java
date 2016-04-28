/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

namespace Flex.Models
{
    public class KeyResult
    {
        public string keyId { get; set; }
        public DerPublicKey der { get; set; }
        public JsonWebKey jwk { get; set; }
    }
}