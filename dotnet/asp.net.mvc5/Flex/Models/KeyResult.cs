
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Flex.Models
{
    public class KeyResult
    {
        public string keyId { get; set; }
        public DerPublicKey der { get; set; }
        public JsonWebKey jwk { get; set; }
    }
}