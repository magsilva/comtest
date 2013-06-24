using System.Collections.Generic;
using System.Linq;
using System.Text;
using System;

namespace VSExamples
{
    public class Siirra
    {
        /// <summary>
        /// Poistetaan jonosta enintaan muuttujan maara osoittama
        /// maara joukon siirrettavat merkkeja.
        /// </summary>
        /// <param name="jono">Merkkijono josta lahdetaan poistamaan</param>
        /// <param name="siirrettavat">Merkit mitka halutaan poistaa</param>
        /// <param name="maara">Montako korkeintaan poistetaan jonosta</param>
        /// <returns>Poistetut merkit</returns>
        /// @example
        /// <pre name="test">
        /// StringBuilder s = new StringBuilder("saippuakauppias");
        /// String t = Siirra.Kirjaimet(s, "s", 3);
        /// s.ToString() === "aippuakauppia";
        /// t === "ss";
        /// t = Siirra.Kirjaimet(s, "a", 2);
        /// s.ToString() === "ippukauppia";
        /// t === "aa";
        /// t = Siirra.Kirjaimet(s, "x", 3);
        /// s.ToString() === "ippukauppia";
        /// t === "";
        /// </pre>
        /// @endexample
        public static String Kirjaimet( StringBuilder jono, String siirrettavat, int maara )
        {
            int i = 0;
            int lkm = 0; // montako merkkia on loydetty
            StringBuilder tulos = new StringBuilder();

            while ( i < jono.Length && lkm < maara )
            {
                char merkki = jono[i];
                if ( siirrettavat.IndexOf( merkki ) >= 0 )
                {
                    jono.Remove( i, 1 );
                    tulos.Append( merkki );
                    lkm++;
                }
                else
                {
                    i++;
                }
            }
            return tulos.ToString();
        }
    }
}
