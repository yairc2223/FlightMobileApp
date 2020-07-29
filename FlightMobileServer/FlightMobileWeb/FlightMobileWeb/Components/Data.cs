using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Newtonsoft.Json;


namespace FlightMobileWeb.Components
{
    public class Data
    {
        public Data() { }

        [JsonProperty("aileron")]
        public float Aileron { get; set; }

        [JsonProperty("rudder")]
        public float Rudder { get; set; }

        [JsonProperty("elevator")]
        public float Elevator { get; set; }

        [JsonProperty("throttle")]
        public float Throttle { get; set; }
    }
}
