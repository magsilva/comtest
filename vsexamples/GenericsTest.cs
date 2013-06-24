using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace VSExamples
{
    public class GenericsTest<GenType>
    {
        /// <pre name="test">
        ///   uint[,] t = { {1,2,3},{4,5,6}};
        ///   uint[] a = GenericsTest<uint>.MultiToOne<uint>(t);
        ///   string.Join(" ",a) === "1 2 3 4 5 6";
        /// </pre>
        /// </example>
        public static T[] MultiToOne<T>( T[,] array )
        {
            int ny = array.GetLength( 0 );
            int nx = array.GetLength( 1 );
            T[] result = new T[ny * nx];
            for ( int iy = 0; iy < ny; iy++ )
            {
                for ( int ix = 0; ix < nx; ix++ )
                    result[iy * nx + ix] = array[iy, ix];
            }
            return result;
        }

        /// <pre name="test">
        ///   GenericsTest<uint> test = new GenericsTest<uint>();
        ///   uint[,] t = { {1,2,3},{4,5,6}};
        ///   uint[] a = test.MultiToOne(t);
        ///   string.Join(" ",a) === "1 2 3 4 5 6";
        /// </pre>
        /// </example>
        public GenType[] MultiToOne( GenType[,] array )
        {
            int ny = array.GetLength( 0 );
            int nx = array.GetLength( 1 );
            GenType[] result = new GenType[ny * nx];
            for ( int iy = 0; iy < ny; iy++ )
            {
                for ( int ix = 0; ix < nx; ix++ )
                    result[iy * nx + ix] = array[iy, ix];
            }
            return result;
        } 
    }
}
