// ComTest - Comments for testing
package comtest.csharp;

import comtest.utils.Strings;
import static comtest.utils.UuidUtilities.*;

/**
 * Writes sln files
 * @author tojukarp
 */
public class CSSolutionWriter {
    public static Strings write(CSSolutionFile source) {
        Strings dest = new Strings();

        dest.add("");
        dest.add("Microsoft Visual Studio Solution File, Format Version 11.00");
        dest.add("# Visual Studio 2010");

        for ( CSSolutionFile.ProjectEntry projEntry : source.projects ) {
            dest.add( String.format( "Project(\"%s\") = \"%s\", \"%s\", \"%s\"",
                    uuidToStr(projEntry.typeGuid, true), projEntry.assemblyName,
                    projEntry.fileName, uuidToStr(projEntry.guid, true) ) );
            dest.add(projEntry.otherData);
            dest.add("EndProject");
        }

        dest.add("Global");

        dest.add( makeGlobalSection(source.getSolutionConfigPlatforms()) );
        dest.add( makeGlobalSection(source.getProjectConfigPlatforms()) );

        for ( CSSolutionFile.GlobalSection globalSect : source.globalSections.values() ) {
            if ( globalSect instanceof CSSolutionFile.SolutionConfigPlatforms ||
                 globalSect instanceof CSSolutionFile.ProjectConfigPlatforms )
                    continue;

            dest.add( makeGlobalSection(globalSect) );
        }

        dest.add("EndGlobal");

        return dest;
    }

    private static Strings makeGlobalSection(CSSolutionFile.GlobalSection sect) {
        if ( sect == null )
            return new Strings();

        Strings dest = new Strings();
        dest.add(String.format("  GlobalSection(%s) = %s", sect.getName(), sect.getValue()));

        for ( String sectString : sect.getContent() ) {
            dest.add("  " + sectString);
        }

        dest.add("  EndGlobalSection");
        return dest;
    }
}
