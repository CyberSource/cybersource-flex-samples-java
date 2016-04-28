/**
* Copyright (c) 2016 by CyberSource
* Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
*/

using System.Web;
using System.Web.Mvc;
using System.Web.Routing;

namespace Flex
{
    public class MvcApplication : HttpApplication
    {
        protected void Application_Start()
        {
            AreaRegistration.RegisterAllAreas();
            FilterConfig.RegisterGlobalFilters(GlobalFilters.Filters);
            RouteConfig.RegisterRoutes(RouteTable.Routes);
        }
    }
}
