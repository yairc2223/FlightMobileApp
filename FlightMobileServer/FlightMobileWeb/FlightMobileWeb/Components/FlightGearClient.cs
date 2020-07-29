using Microsoft.Extensions.Configuration;
using FlightMobileWeb.Components;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace FlightMobileWeb
{
    public class FlightGearClient
    {
        //Instance for Singalton
        private static FlightGearClient instance = null;
        public static FlightGearClient Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new FlightGearClient();
                    instance.Start();
                }
                return instance;
            }
        }

        private readonly BlockingCollection<AsyncCommand> _queue;
        private readonly TcpClient _client;
        volatile public int port = -1;
        volatile public string ip = "none";
        /** constructor **/
        private FlightGearClient()
        {
            _queue = new BlockingCollection<AsyncCommand>();
            _client = new TcpClient();
        }

        // Called by the WebApi Controller, it will await on the returned Task<>
        // This is not an async method, since it does not await anything.
        public Task<Result> Execute(Command cmd)
        {
            var asyncCommand = new AsyncCommand(cmd);
            _queue.Add(asyncCommand);
            return asyncCommand.Task;
        }
        //function starts processCommands function 
        public void Start()
        {
            Task.Factory.StartNew(ProcessCommands);
        }
        
        // function connects to flightGear, and sends data to it.
        public void ProcessCommands()
        {
            while (port == -1 || ip == "none") ;
            while (true)
            {
                try
                {
                    _client.Connect(ip, port);

                    NetworkStream stream = _client.GetStream();
                    // send "data \n" to the simulator
                    byte[] sendBuffer = Encoding.ASCII.GetBytes("data \n");
                    stream.Write(sendBuffer, 0, sendBuffer.Length);
                    HandleQueueCommands(sendBuffer, stream);
                    stream.Close();
                    _client.Close();
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Data);
                }
            }
        }
         
        //function recieves a sendBuffer, and stream to connect to client, sends the commands from the queue 
        //and sets the result of the command
        private void HandleQueueCommands(byte[] sendBuffer, NetworkStream stream)
        {
            foreach (AsyncCommand command in _queue.GetConsumingEnumerable())
            {
                // set
                Result res = ActOnCommand(command, sendBuffer, stream);
                command.Completion.SetResult(res);
            }
        }

        /** method recieves command to send and check, a send buffer and client stream 
         * method sends set command to client, and then checks that value was set in client.
         * method return OK if succeded and NotOk if didnt.
         * **/
        private Result ActOnCommand(AsyncCommand command, byte[] sendBuffer, NetworkStream stream)
        {
            byte[] getBuffer;
            Array.Clear(sendBuffer, 0, sendBuffer.Length);
            sendBuffer = Encoding.ASCII.GetBytes(command.Command.comanndSet);
            byte[] recvBuffer = new byte[1024];
            stream.Write(sendBuffer, 0, sendBuffer.Length);

            //get - checks if the value that we sent to the simulaor corresponds to the value ew get from the simulator
            Array.Clear(sendBuffer, 0, sendBuffer.Length);
            getBuffer = Encoding.ASCII.GetBytes(command.Command.comanndGet);
            recvBuffer = new byte[1024];
            stream.Write(getBuffer, 0, getBuffer.Length);
            int nRead = stream.Read(recvBuffer, 0, 1024);
            string getValue = Encoding.ASCII.GetString(recvBuffer);
            float val = float.Parse(getValue);
            Result res;
            if (val == command.Command.value)
            {
                res = Result.Ok;
            }
            else
            {
                res = Result.NotOk;
            }
            return res;
        }
    }
}
