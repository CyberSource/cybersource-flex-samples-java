/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

using Flex.Models;
using Newtonsoft.Json;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using Org.BouncyCastle.Security;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Web.Configuration;

namespace Flex.Services
{
    interface ISecurityService
    {
        Dictionary<string, string> GenerateSignature();
        Task<KeyResult> FetchKeystore();
        RsaKeyParameters DecodePublicKey(string derEncodedKey);
        bool Verify(AsymmetricKeyParameter publicKey, string dataToVerify, string base64Signature);
    }

    public class SecurityService: ISecurityService
    {
        private static string flexKeysEndpoint = "https://testflex.cybersource.com/cybersource/flex/v1/keys";

        private string mid;
        private string profileId;
        private string cmmKey;
        private string organizationId;
        private string keyStoreFile;
        private string keyStorePassword;
        private string privateKeyPassword;
                
        public SecurityService()
        {
            // Read in flex configuration from web.config    
            mid = WebConfigurationManager.AppSettings["mid"];
            profileId = WebConfigurationManager.AppSettings["profileId"];
            cmmKey = WebConfigurationManager.AppSettings["cmmKey"];
            organizationId = WebConfigurationManager.AppSettings["organizationId"];
            keyStoreFile = WebConfigurationManager.AppSettings["keyStoreFile"];
            keyStorePassword = WebConfigurationManager.AppSettings["keyStorePassword"];
            privateKeyPassword = WebConfigurationManager.AppSettings["privateKeyPassword"];
        }

        private AsymmetricKeyParameter readPrivateKey()
        {
            var certificate = new X509Certificate2(keyStoreFile, keyStorePassword, X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet);

            // Private Key
            RSACryptoServiceProvider rsa = (RSACryptoServiceProvider)certificate.PrivateKey;
            MemoryStream memoryStream = new MemoryStream();
            TextWriter streamWriter = new StreamWriter(memoryStream);
            PemWriter pemWriter = new PemWriter(streamWriter);
            AsymmetricCipherKeyPair keyPair = DotNetUtilities.GetKeyPair(rsa);

            return keyPair.Private;
        }

        public Dictionary<string,string> GenerateSignature()
        {
            try
            {
                // data used to generate signature
                var time = (long)(DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalMilliseconds;
                var contentType = "application/json";
                var httpVerb = "post";

                // Preparing data string that will be used to derive the signature
                var dataToSign = new StringBuilder();
                dataToSign.Append("cmm-key=").Append(cmmKey).Append(',');
                dataToSign.Append("content-type=").Append(contentType).Append(',');
                dataToSign.Append("http-verb=").Append(httpVerb).Append(',');
                dataToSign.Append("organization-id=").Append(organizationId).Append(',');
                dataToSign.Append("time=").Append(time);
                var dataToSignBytes = Encoding.UTF8.GetBytes(dataToSign.ToString());
                                
                // Signing data
                var privateKey = readPrivateKey();                                            

                ISigner signer = SignerUtilities.GetSigner("SHA512withRSA");
                signer.Init(true, privateKey);
                signer.BlockUpdate(dataToSignBytes, 0, dataToSignBytes.Length);
                byte[] sigBytes = signer.GenerateSignature();
                var encoded = Convert.ToBase64String(sigBytes); // transform byte array to Base64 string

                // Return dictionary of header names & values
                return new Dictionary<string, string>()
                {
                    { "Authorization", "CWS CWSAccessKeyId:" + encoded },
                    { "HTTP-Verb", httpVerb },
                    { "Organization-Id", organizationId },
                    { "Time", time.ToString() },
                    { "CMM-Key", cmmKey },
                    { "Content-Type", contentType }
                };
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public async Task<KeyResult> FetchKeystore()
        {
            // prepare keys endpoint request payload
            var payload = new KeyParameters()
            {
                profileId = profileId,
                encryptionType = "WebCryptoAPI"
            };

            // prepare HTTP headers to make server2flex rest call
            var signature = GenerateSignature();            
            
            byte[] buffer = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(payload));

            // TODO this should probably be rewritten to use the newer HttpClient from .net 4.5
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(flexKeysEndpoint);
            request.Method = signature["HTTP-Verb"];
            request.Headers.Add("X-MERCHANT-ID", mid);
            request.Headers.Add("Authorization", signature["Authorization"]);
            request.Headers.Add("HTTP-Verb", signature["HTTP-Verb"]);
            request.Headers.Add("Organization-Id", signature["Organization-Id"]);
            request.Headers.Add("Time", signature["Time"]);
            request.Headers.Add("CMM-Key", signature["CMM-Key"]);
            request.ContentType = signature["Content-Type"];
            request.ContentLength = buffer.Length;

            var reqStream = request.GetRequestStream();
            reqStream.Write(buffer, 0, buffer.Length);
            reqStream.Close();            

            using (var response = (HttpWebResponse)request.GetResponse())
            {
                var encoding = Encoding.UTF8;

                using (var responseStream = response.GetResponseStream())
                using (var reader = new StreamReader(responseStream, encoding))
                {
                    var result = reader.ReadToEnd();
                    return await Task.Factory.StartNew(() =>
                    {
                        return JsonConvert.DeserializeObject<KeyResult>(result);
                    });
                }                    
            }            
        }

        public RsaKeyParameters DecodePublicKey(string derEncodedKey)
        {
            try
            {
                byte[] keyBytes = Convert.FromBase64String(derEncodedKey);
                return (RsaKeyParameters)PublicKeyFactory.CreateKey(keyBytes);
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public bool Verify(AsymmetricKeyParameter publicKey, string dataToVerify, string base64Signature)
        {
            try
            {
                var dataToVerifyBytes = Encoding.UTF8.GetBytes(dataToVerify.ToString());

                ISigner signer = SignerUtilities.GetSigner("SHA512withRSA");
                signer.Init(false, publicKey);
                signer.BlockUpdate(dataToVerifyBytes, 0, dataToVerifyBytes.Length);
                var signature = Convert.FromBase64String(base64Signature);
                return signer.VerifySignature(signature);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
    }
}