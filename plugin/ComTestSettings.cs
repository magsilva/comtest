using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using EnvDTE80;

namespace JYU.ComTestCSPlugin
{
    [Serializable]
    public class ComTestSettings : ICloneable
    {
        public string JavaPath { get; set; }
        public string ComtestPath { get; set; }

        public static ComTestSettings Load( DTE2 appObject )
        {
            return appObject.Globals.get_VariableExists( "ComTestData" ) ?
                        ComTestSettings.LoadFromString( appObject.Globals["ComTestData"] as string ) :
                        ComTestSettings.LoadDefault();
        }

        public void Save( DTE2 appObject )
        {
            appObject.Globals["ComTestData"] = SaveToString();
            appObject.Globals.set_VariablePersists( "ComTestData", true );
        }

        public String SaveToString()
        {
            var ser = new XmlSerializer( typeof( ComTestSettings ) );
            var writer = new MemoryStream();
            ser.Serialize( writer, this );
            return Convert.ToBase64String( writer.GetBuffer() );
        }

        public static ComTestSettings LoadFromString( String s )
        {
            byte[] data = Convert.FromBase64String( s );
            var ser = new XmlSerializer( typeof( ComTestSettings ) );
            var reader = new MemoryStream( data );

            // The first time using add-in so load the default settings
            var settings = ser.Deserialize( reader ) as ComTestSettings ?? LoadDefault();

            return settings;
        }

        #region ICloneable Members

        public object Clone()
        {
            return MemberwiseClone() as ComTestSettings;
        }

        #endregion

        public static ComTestSettings LoadDefault()
        {
            return new ComTestSettings
            {
                JavaPath = "java.exe",
                ComtestPath = "C:\\ComTest\\ComTest.jar"
            };
        }
    }
}
