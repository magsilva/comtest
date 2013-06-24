using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VSExamples
{
    public class Exceptions
    {
        /// <summary>
        /// Returns the length of a string without null check
        /// </summary>
        /// <param name="str"></param>
        /// <returns></returns>
        /// @example
        /// <pre name="test">
        /// Exceptions.StringLength(null) === 0; #THROWS NullReferenceException
        /// </pre>
        /// @endexample
        public static int StringLength(String str)
        {
            return str.Length;
        }
    }
}
