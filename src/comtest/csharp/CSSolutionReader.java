// ComTest - Comments for testing
package comtest.csharp;

import comtest.utils.Strings;
import static comtest.utils.StringUtilities.*;
import static comtest.utils.UuidUtilities.*;

/**
 * Reads sln files
 * @author tojukarp
 */
public class CSSolutionReader {
    public static CSSolutionFile read(Strings source) {
        if ( !IsSolution(source) )
            return null;

        CSSolutionFile dest = new CSSolutionFile();
        CSSolutionFile.ProjectEntry projEntry = null;
        CSSolutionFile.GlobalSection globalSect = null;

        for ( int i = 0; i < source.size(); i++ ) {
            String sRaw = source.get(i);
            String s = sRaw.trim();

            if ( projEntry != null ) {
                if ( s.equals("EndProject") ) {
                    dest.projects.add(projEntry);
                    projEntry = null;
                }
                else
                    projEntry.otherData.add(sRaw);
            }
            else if ( globalSect != null ) {
                if ( s.equals("EndGlobalSection") ) {
                    dest.globalSections.put(globalSect.getName(), globalSect);
                    globalSect = null;
                }
                else
                    globalSect.addContent(sRaw);
            }
            else if ( indexOfNotInQuotes(s, "Project(") == 0 ) {
                projEntry = new CSSolutionFile.ProjectEntry();
                projEntry.typeGuid = strToUuid( s.substring(9, 47) );
                String temp = s.substring(53);
                String[] elements = temp.split("(\", \")|\"");
                projEntry.assemblyName = elements[0];
                projEntry.fileName = elements[1];
                projEntry.guid = strToUuid( elements[2] );
            }
            else if ( indexOfNotInQuotes(s, "GlobalSection(") == 0 ) {
                int endName = indexOfNotInQuotes(s, ")");
                String sectName = s.substring(14, endName);

                if ( sectName.equals(CSSolutionFile.SolutionConfigPlatforms.getSectionName()) )
                    globalSect = new CSSolutionFile.SolutionConfigPlatforms();
                else if ( sectName.equals(CSSolutionFile.ProjectConfigPlatforms.getSectionName()) )
                    globalSect = new CSSolutionFile.ProjectConfigPlatforms();
                else {                    
                    int startValue = indexOfNotInQuotes(s, " = ") + 3;
                    globalSect = new CSSolutionFile.GlobalSection(sectName, s.substring(startValue));
                }
            }
        }

        return dest;
    }

    private static boolean IsSolution(Strings source) {
        int contentLines = 0;

        for ( int i = 0; i < source.size(); i++ ) {
            String s = source.get(i);

            if ( s.length() == 0 )
                continue;

            if ( contentLines++ > 1 )
                return false;

            if ( s.indexOf("Microsoft Visual Studio Solution File") >= 0 )
                return true;
        }

        return false;
    }
}
