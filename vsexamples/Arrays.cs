using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace B1
{
    public class Taulukot
    {
        public static void Main(string[] args)
        {
            double[,] mat1 = { { 1, 2, 3 }, { 2, 2, 2 }, { 4, 2, 3 } };
            double[,] mat2 = { { 9, 2, 8 }, { 1, 2, 5 }, { 3, 19, -3 } };
            double suurin1 = Suurin(mat1);
            double suurin2 = Suurin(mat2);
            Console.ReadKey();

        }

        /// <summary>
        /// etsii 2-ulotteisen reaalilukutaulukon suurimman alkion
        /// </summary>
        /// <param name="matriisi"></param>
        /// <returns></returns>
        /// @example
        /// <pre name="test">
        /// Taulukot.Suurin(new double[,] { { 1, 2, 3 }, { 2, 2, 2 }, { 4, 2, 3 } }) ~~~ 4;
        /// Taulukot.Suurin(new double[,] { { 9, 2, 8 }, { 1, 2, 5 }, { 3, 19, -3 } }) ~~~ 19;
        /// </pre>
        /// @endexample
        public static double Suurin(double[,] matriisi)
        {
            double suurin = double.MinValue;
            foreach (double luku in matriisi)
            {
                if (luku > suurin) suurin = luku;
            }
            return suurin;
        }

        /// <summary>
        /// Aliohjelma muuttaa annetun merkkijonon kokonaislukutaulukoksi
        /// siten, etta eri luvut erotellaan annetun merkkitaulukon (erotinmerkkien)
        /// perusteella.
        /// </summary>
        /// <param name="lukusyote">Muunnettava merkkijono</param>
        /// <param name="erottimet">Sallitut erotinmerkit merkkitaulukossa</param>
        /// <returns>Merkkijonosta selvitetty kokonaislukutaulukko.</returns>
        /// @example
        /// <pre name="test">
        /// int[] tulos = Taulukot.MerkkijonoLuvuiksi("1 2 3", new char[]{' '});
        /// tulos[$i] === $luku;
        /// 
        /// $i | $luku
        /// ------------------
        /// 0  |  1
        /// 1  |  2
        /// 2  |  3
        /// </pre>
        /// @endexample
        public static int[] MerkkijonoLuvuiksi( string lukusyote, char[] erottimet )
        {
            // Tyhjat pois edesta ja lopusta (Trim())            
            String[] pilkottu = lukusyote.Trim().Split( erottimet );
            int[] luvut = new int[pilkottu.Length]; // luvut[] saa kookseen saman kuin pilkottu[]
            for ( int i = 0; i < pilkottu.Length; i++ )
            {
                if ( String.IsNullOrEmpty( pilkottu[i] ) )
                {
                    // Jos taulukosta loytyy tyhja merkkijono (tai null-arvo), 
                    // asetetaan luvut[i] arvoksi 0 ja j‰tet‰‰n silmukan loppuosa silt‰
                    // kierrokselta suorittamatta (continue-lause)
                    luvut[i] = 0;
                    continue;
                }
                luvut[i] = int.Parse( pilkottu[i] );
            }
            return luvut;
        }

        /// <summary>
        /// Lasketaan yhteen kaksi matriisia ja palautetaan uusi matriisi
        /// jonka koon m‰‰r‰‰ ensimm‰isen rivin koko.
        /// </summary>
        /// <param name="a">Matriisi 1</param>
        /// <param name="b">Matriisi 2</param>
        /// <returns>a+b komponenteittain laskettuna</returns>
        /// @example
        /// <pre name="test">
        ///  double[,] mat1 = {{1,2,3},{2,2,2},{4,2,3}};
        ///  double[,] mat2 = {{9,2,8},{1,2,5},{3,19,-3}};
        ///  double[,] mat3 = Taulukot.Summaa(mat1,mat2);
        ///  mat3[$i,$j] ~~~ mat1[$i,$j] + mat2[$i,$j];
        ///  
        /// $i | $j
        /// ---------
        /// 0  |  0
        /// 0  |  1
        /// 0  |  2
        /// 1  |  0
        /// 1  |  1
        /// 1  |  2
        /// 2  |  0
        /// 2  |  1
        /// 2  |  2
        /// </pre>
        /// @endexample
        public static double[,] Summaa( double[,] a, double[,] b )
        {
            double[,] c = new double[a.GetLength( 0 ), a.GetLength( 1 )];

            for ( int i = 0; i < c.GetLength(0); i++ )
                for ( int j = 0; j < c.GetLength( 1 ); j++ )
                    c[i,j] = a[i,j] + b[i,j];

            return c;
        }
    }
}
