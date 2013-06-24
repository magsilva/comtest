using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VSExamples
{
    public class RegExes
    {
        static Random rand = new Random();

        /// <summary>
        /// Concatenates two strings and generates random (a-z) characters
        /// between them.
        /// </summary>
        /// <param name="s1">First string</param>
        /// <param name="s2">Second string</param>
        /// <param name="randChars">Number of random characters to add</param>
        /// <returns>New string</returns>
        /// @example
        /// <pre name="test">
        /// string test = RegExes.RandomConcat("saippua", "kauppias", 4);
        /// test =R= "saippua.*kauppias";
        /// test =~ "saippua....kauppias";
        /// </pre>
        public static string RandomConcat(string s1, string s2, int randChars)
        {
            StringBuilder result = new StringBuilder( s1 );

            for ( int i = 0; i < randChars; i++ )
                result.Append( (char)rand.Next( (int)'a', (int)'z' ) );

            return result.Append( s2 ).ToString();
        }
    }
}
