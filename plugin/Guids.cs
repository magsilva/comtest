// Guids.cs
// MUST match guids.h
using System;

namespace JYU.ComTestCSPlugin
{
    static class GuidList
    {
        public const string guidComTestCSPluginPkgString = "57d219a9-87b8-45ba-ae9b-948e468146ce";
        public const string guidComTestCSPluginCmdSetString = "6f3a310d-eec9-4054-a7eb-69c798cf1fe9";

        public static readonly Guid guidComTestCSPluginCmdSet = new Guid(guidComTestCSPluginCmdSetString);
    };
}