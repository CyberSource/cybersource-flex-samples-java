using Flex.Services;
using Org.BouncyCastle.Crypto.Parameters;
using System;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Mvc;

namespace Flex.Controllers
{
    public class CheckoutController : Controller
    {
        private ISecurityService securityService = new SecurityService();

        [HttpGet]
        public ActionResult Index()
        {
            return RedirectToAction("Checkout");
        }

        [HttpGet]
        [Route("checkout")]
        public async Task<ActionResult> Checkout()
        {
            // Add JSON Web Keystore to the view model and return rendered page.
            var key = await securityService.FetchKeystore();
            ViewBag.jwk = key.jwk;

            // parse Flex public key in DER format and store it in session for future use. 
            var flexPublicKey = securityService.DecodePublicKey(key.der.publicKey);
            Session.Add("flexPublicKey", flexPublicKey);

            return View();            
        }        

        [HttpPost]
        public ActionResult Receipt(FormCollection form)
        {
            // Read in the public key to use and remove it from the session
            var flexPublicKey = (RsaKeyParameters)Session["flexPublicKey"];
            Session.Remove("flexPublicKey");

            // Verify Flex signature passed as POST parameters
            var signedFields = form["flex_signedFields"].Split(',').Select(x => "flex_" + x);
            var signedValues = string.Join(",", signedFields.Select(x => form[x]));
            
            if (!securityService.Verify(flexPublicKey, signedValues, form["flex_signature"]))
            {
                throw new Exception("The signature is not valid");
            }

            // All posted params and values added to a dictionary for display on the receipt page.
            var model = form.AllKeys.ToDictionary(k => k, k => form[k]);
            return View(model);
        }
    }
}