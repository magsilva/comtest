using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;

namespace VSExamples
{
    public class Calc
    {
        /// <summary>
        /// Summaa kaksi kokonaislukua.
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns>a+b</returns>
        /// @example
        /// <pre name="test">
        /// Calc c = new Calc();
        /// int x = c.Add(4, 6);
        /// x === 10;
        /// </pre>
        /// @endexample
        public int Add( int a, int b )
        {
            return a + b;
        }

        /// <summary>
        /// Summaa kaksi liukulukua.
        /// </summary>
        /// <param name="a"></param>
        /// <param name="b"></param>
        /// <returns>a+b</returns>
        /// @example
        /// <pre name="test">
        /// Calc c = new Calc();
        /// double x = c.Add(4.5, 6.4);
        /// x ~~~ 10.9;
        /// </pre>
        /// @endexample
        public double Add( double a, double b )
        {
            return a + b;
        }

        // <summary>
        // Kertoo kaksi liukulukua.
        // </summary>
        // <param name="a"></param>
        // <param name="b"></param>
        // <returns>a*b</returns>
        // @example
        // <pre name="test">
        // double test = Calc.Multiply(0.14, 2.71);
        // test ~~~ 0.3794;
        // </pre>
        // @endexample
        public static double Multiply( double a, double b )
        {
            return a * b;
        }

        /*
         * @example
         * <pre name="test">
         * Calc.Subtract(3, 1.4) ~~~ 1.6;
         * </pre>
         * @endexample
         */
        public static double Subtract( double a, double b )
        {
            return a - b;
        }
    }
}
