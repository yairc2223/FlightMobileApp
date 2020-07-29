using System;
using System.Collections.Generic;
using System.Configuration;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;

namespace FlightMobileWeb.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class ScreenshotController : Controller
    {
        IConfiguration configuration;

        public ScreenshotController(IConfiguration iConfig)
        {
            configuration = iConfig;
        }

        /* Get screenshot */
        [HttpGet]
        public async Task<ActionResult> Get()
        {
            string port = configuration.GetSection("ScreenshotPort").Value;
            string ip = configuration.GetSection("SimulatorIP").Value;
            try
            {
                
                var url = "http://" + ip + ":" + port + "/screenshot?type=jpeg";
                WebRequest request = WebRequest.Create(url);
                request.Credentials = CredentialCache.DefaultCredentials;
                WebResponse response = await request.GetResponseAsync();
                // Get the stream containing content returned by the server.
                // The using block ensures the stream is automatically closed.
                using (Stream dataStream = response.GetResponseStream())
                {
                    MemoryStream ms = new MemoryStream();
                    dataStream.CopyTo(ms);
                    response.Close();
                    byte[] bytes = ms.ToArray();
                    return File(bytes, "image/jpeg");
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            Response.StatusCode = (int)HttpStatusCode.BadRequest;
            return Content("Bad connection to FlightGear");
        }
    }
}