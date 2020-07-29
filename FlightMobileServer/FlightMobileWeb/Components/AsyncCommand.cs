using FlightMobileWeb.Components;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace FlightMobileWeb
{
    public enum Result { Ok, NotOk }

    // command class include commandSet,comanndGet and the value
    public class Command
    {
        public string comanndSet { get; set; }
        public string comanndGet { get; set; }
        public float value { get; set; }
    }
    public class AsyncCommand
    {
        public Command Command { get; private set; }
        public Task<Result> Task { get => Completion.Task; }
        public TaskCompletionSource<Result> Completion { get; private set; }
        public AsyncCommand(Command input)
        {
            Command = input;
            // Watch out! Run Continuations Async is important!
            Completion = new TaskCompletionSource<Result>(
            TaskCreationOptions.RunContinuationsAsynchronously);
        }
    }
}