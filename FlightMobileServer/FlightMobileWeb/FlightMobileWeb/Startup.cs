using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

namespace FlightMobileWeb
{
    public class SimulatorPort
    {
        public string port { get; set; }
    }
    public class Startup
    {
            public IConfiguration Configuration { get; set; }
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }
        // This method gets called by the runtime. Use this method to add services to the container.
        // For more information on how to configure your application, visit https://go.microsoft.com/fwlink/?LinkID=398940
        public void ConfigureServices(IServiceCollection services)
        {
            // Add Cors

            services.AddRouting();
            services.AddControllers();
            services.AddMvc();
            services.Configure<SimulatorPort>(Configuration.GetSection("SimulatorPort"));
            services.AddSingleton(Configuration);
            

        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            app.UseDefaultFiles();
            app.UseStaticFiles();
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            //EnableCorsAttribute x = new EnableCorsAttribute();
            /*app.UseCors("MyPolicy");
            app.Use(async (context, next) =>
            {
                var url = context.Request.Path.Value;

                // Rewrite to index
                if (url.Contains("/api/screenshot"))
                {
                    // rewrite and continue processing
                    context.Response.Redirect("http://localhost:8080/screenshot?type=png");
                    
                    return;
                }

                await next();
            });*/
            app.UseResponseCaching();
            app.UseRouting();
            app.UseAuthentication();
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllerRoute(
                    name: "default",
                    pattern: "{controller}/{action=Index}/{id?}");
            });


        }
    }
}
