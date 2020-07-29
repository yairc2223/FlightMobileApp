using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using FlightMobileWeb.Components;

namespace FlightMobileWeb.Controllers
{
    [Route("api/command")]
    [ApiController]
    public class CommandController : Controller
    {
        IConfiguration Configuration { get; set; }
        /** constructor**/
        public CommandController(IConfiguration iConfig)
        {
            Configuration = iConfig;
            //connection initialization, if not initialized
            if(Configuration == null)
            {
                return;
            }
            string ip = Configuration.GetSection("SimulatorIP").Value;
            if(ip == null)
            {
                ip = "localhost"; 
            }
            string SimulatorPort = Configuration.GetSection("SimulatorPort").Value;
            int port = -1, o;
            if(int.TryParse(SimulatorPort, out o))
            {
                port = int.Parse(SimulatorPort);
            }
            if(FlightGearClient.Instance.port == -1)
            {
                FlightGearClient.Instance.port = port;
            }
            if (FlightGearClient.Instance.ip == "none")
            {
                FlightGearClient.Instance.ip = ip;
            }
        }

        /** Post method, recieved Data object and return Http code to notify what happned **/
        [HttpPost]
        public HttpResponseMessage Post([FromBody] Data value)
        {
            Result res;
            if (value == null)
            {
                return new HttpResponseMessage(System.Net.HttpStatusCode.BadRequest);
            }
            else
            {
                res = sendToSim(value);
            }
            if(res == Result.NotOk)
            {
                return new HttpResponseMessage(System.Net.HttpStatusCode.InternalServerError);

            }
            return new HttpResponseMessage(System.Net.HttpStatusCode.OK);
        }
        /***
         * method creates and returns a command object, with the recieved values as its fields values 
         ***/
        private Command createCommand(float value, string getCommand, string setCommand)
        {
            Command cmd = new Command();
            cmd.value = value;
            cmd.comanndGet = getCommand;
            cmd.comanndSet = setCommand;
            return cmd;
        }

        /***
        Send data to the simulator
        ***/
        private Result sendToSim(Data value)
        {
            // set commands
            string aileronSet = "set /controls/flight/aileron " + value.Aileron + "\n";
            string rudderSet = "set /controls/flight/rudder " + value.Rudder + "\n";
            string elevatorSet = "set /controls/flight/elevator " + value.Elevator + "\n";
            string throttleSet = "set /controls/engines/current-engine/throttle " + value.Throttle + "\n";

            // get commands
            string aileronGet = "get /controls/flight/aileron " + "\n";
            string rudderGet = "get /controls/flight/rudder " + "\n";
            string elevatorGet = "get /controls/flight/elevator " + "\n";
            string throttleGet = "get /controls/engines/current-engine/throttle " + "\n";

            // aileron command - include comanndSet,comanndGet and the value
            Command aileronCommand = createCommand(value.Aileron, aileronGet, aileronSet);
            // rudder command - include comanndSet,comanndGet and the value
            Command rudderCommand = createCommand(value.Rudder, rudderGet, rudderSet);
            // elevator command - include comanndSet,comanndGet and the value
            Command elevatorCommand = createCommand(value.Elevator, elevatorGet, elevatorSet);
            // throttle command - include comanndSet,comanndGet and the value
            Command throttleCommand = createCommand(value.Throttle, throttleGet, throttleSet);

            var commands = new List<Command>
            {
                aileronCommand,
                rudderCommand,
                elevatorCommand,
                throttleCommand
            };

            Result res = ActOnCommands(commands);
            if (res == Result.NotOk)
            {
                return Result.NotOk;
            }
            return Result.Ok;
        }
            /*Task<Result> resAileron = sendCommand(aileronCommand);
            if (resAileron.Result != Result.Ok)
            {
                return Result.NotOk;
                // RETURN ERROR 
            }
            Task<Result> resRudder = sendCommand(rudderCommand);
            if (resRudder.Result != Result.Ok)
            {
                return Result.NotOk;
                // RETURN ERROR 
            }
            Task<Result> resElevator = sendCommand(elevatorCommand);
            if (resElevator.Result != Result.Ok)
            {
                return Result.NotOk;
                // RETURN ERROR 
            }
            Task<Result> resThrottle = sendCommand(throttleCommand);

            if (resThrottle.Result != Result.Ok)
            {
                return Result.NotOk;
                // RETURN ERROR 
            }
            return Result.Ok;*/
        
        /* method recieves a list of commands to send to client, and returns if execution was OK or NotOk */
        private Result ActOnCommands(List<Command> commands)
        {
            foreach(var command in commands)
            {
                Task<Result> res = sendCommand(command);
                if(res.Result != Result.Ok)
                {
                    return Result.NotOk;
                }
            }
            return Result.Ok;
        }
        /* method recieves a command to execute, and return the execution result */
        private async Task<Result> sendCommand(Command command)
        {
            return await FlightGearClient.Instance.Execute(command);
        }
    }
}