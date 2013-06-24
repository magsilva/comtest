// ComTest - Comments for testing
package cc.jyu.fi.comtest;

import cc.jyu.fi.comtest.utils.Strings;

import java.io.File;
import java.io.IOException;

import static cc.jyu.fi.comtest.ComTestSyntax.*;
import static cc.jyu.fi.comtest.utils.FileUtilities.*;
import static cc.jyu.fi.comtest.utils.StringUtilities.*;

/**
 * Configuration for ComTest
 * @author tojukarp
 */
public class ComTestIni {
    public String directory;
    public String packageName;
    public Strings imports;

    public boolean doStaticImport = false;
    public boolean noImportAtAll = false;
    public boolean fullAutoImport = true;

    public boolean packageImport = false;
    public boolean classImport = false;
    public boolean dynamicImport = false;

    public ComTestIni() {
        imports = new Strings();
        packageName = null;
        directory = null;
    }

    public ComTestIni(String dir) {
        this();
        directory = dir;

        String iniDir = findFirstDirectory(dir, COMTESTINI);

        if ( iniDir != null ) {
            String fileName = addPath(iniDir, COMTESTINI);
            Strings contents = getFileContents(fileName);
            parseOptions(contents);
        }        
    }

    /**
     * Check the selected contents by ComTest options
     * @param contents what to check
     * @param all to check all or only part
     *
     */
    private void parseOptions(Strings contents) {
        if ( contents == null ) return;

        for (int i=0; i < contents.size(); i++ ) {
            parseOptionLine(contents.get(i));
        }
    }

    /**
     * Check the selected line by ComTest options
     * @param line what to check
     * @param all to check all or only part
     */
    private void parseOptionLine(String line) {
        String s = line.trim();
        if ( s.matches("^\\* ?[^ ].*") ) s = s.substring(1).trim();     // allow: * #import anywhere
        else if ( s.matches("^// ?[^ ].*") ) s = s.substring(2).trim(); // or: // #import
        s = removeAllAfter(s, "//");
        if ( handleDirAndPackage(s) ) return;
        if ( handleImports(s) ) return;
        if ( checkVariables(s) ) return;
    }

    /**
     * Handles directory and package specific lines
     * @param line line to handle
     * @return true if handled
     */
    private boolean handleDirAndPackage(String line) {
        String s = line.trim();
        if ( indexOfNotInQuotes(s, DIRECTORYMARKER) == 0 ) {
            directory = s.substring(DIRECTORYMARKER.length()).trim();
            return true;
        }

        if ( indexOfNotInQuotes(s, PACKAGEMARKER) == 0 ) {
            packageName = s.substring(PACKAGEMARKER.length()).trim();
            return true;
        }
        return false;
    }

    /**
     * Checks if s is for setting imports.  If is, then true
     * is returned and the import is handled.
     * @param line line to check
     * @return true if import line, false otherwise
     */
    private boolean handleImports(String line) { // NOPMD by vesal on 13.1.2008 21:05
        String s = line.trim();

        if ( indexOfNotInQuotes(s, NOIMPORT) == 0 ) {
            imports.clear();
            doStaticImport = false;
            // noAutoImport = true;
            fullAutoImport = false;
            if ( indexOfNotInQuotes(s, "=all") > 0 ) noImportAtAll = true;
            return true;
        }
        if ( indexOfNotInQuotes(s, STATICIMPORT) == 0 ) {
            doStaticImport = true;
            // noAutoImport = true;
            fullAutoImport = false;
            return true;
        }
        if ( indexOfNotInQuotes(s, DYNAMICIMPORT) == 0 ) {
            dynamicImport = true;
            fullAutoImport = false;
            return true;
        }
        if ( indexOfNotInQuotes(s, CLASSIMPORT) == 0 ) {
            classImport = true;
            fullAutoImport = false;
            return true;
        }
        if ( indexOfNotInQuotes(s, PACKAGEIMPORT) == 0 ) {
            packageImport = true;
            return true;
        }
        if ( indexOfNotInQuotes(s, IMPORTMARKER) == 0 ) {
            String imp = s.substring(IMPORTMARKER.length() + 1).trim();
            imports.add(imp);
            // noAutoImport = true;
            return true;
        }

        return false;
    }

    /**
     * Check if d is substitution to any know variable
     * @param st string to check
     * @return true if any variable, false otherwise
     */
    public static boolean checkVariables(String st) { // NOPMD by vesal on 13.1.2008 15:29
        String val;
        String s = removeAllAfter(st,";").trim();
        val = checkVariable(s, "COLUMNSEPARATOR"     ); if ( val != null ) { COLUMNSEPARATOR      = val; return true; }
        val = checkVariable(s, "TEMPLATELINEMARKER"  ); if ( val != null ) { TEMPLATELINEMARKER   = val; return true; }
        val = checkVariable(s, "DELETESENTENCEMARKER"); if ( val != null ) { DELETESENTENCEMARKER = val; return true; }
        val = checkVariable(s, "COLUMNCHAR"          ); if ( val != null ) { COLUMNCHAR           = val; return true; }
        val = checkVariable(s, "INITLINE"            ); if ( val != null ) { INITLINE             = val; return true; }
        val = checkVariable(s, "EVERYLINEINITS1"     ); if ( val != null ) { EVERYLINEINITS1      = val; return true; }
        val = checkVariable(s, "EVERYLINEINITS2"     ); if ( val != null ) { EVERYLINEINITS2      = val; return true; }
        val = checkVariable(s, "EQUALSMARKER"        ); if ( val != null ) { EQUALSMARKER         = val; return true; }
        val = checkVariable(s, "EQUALSMARKER2"       ); if ( val != null ) { EQUALSMARKER2        = val; return true; }
        val = checkVariable(s, "ALMOSTMARKER1"       ); if ( val != null ) { ALMOSTMARKER1        = val; return true; }
        val = checkVariable(s, "ALMOSTMARKER2"       ); if ( val != null ) { ALMOSTMARKER2        = val; return true; }
        val = checkVariable(s, "REGEXPMARKER1"       ); if ( val != null ) { REGEXPMARKER1        = val; return true; }
        val = checkVariable(s, "REGEXPMARKER2"       ); if ( val != null ) { REGEXPMARKER2        = val; return true; }
        val = checkVariable(s, "TOLERANCE"           ); if ( val != null ) { TOLERANCE            = val; return true;}
        val = checkVariable(s, "THROWSMARKER"        ); if ( val != null ) { THROWSMARKER         = val; return true; }
        val = checkVariable(s, "IMPORTMARKER"        ); if ( val != null ) { IMPORTMARKER         = val; return true; }
        val = checkVariable(s, "PACKAGEMARKER"       ); if ( val != null ) { PACKAGEMARKER        = val; return true; }
        val = checkVariable(s, "DIRECTORYMARKER"     ); if ( val != null ) { DIRECTORYMARKER      = val; return true; }
        val = checkVariableQ(s, "BEFORETESTCLASS"    ); if ( val != null ) { BEFORETESTCLASS      = val; return true; }
        val = checkVariableQ(s, "JUNITIMPORTS"       ); if ( val != null ) { JUNITIMPORTS         = val; return true; }
        return false;
    }

    /**
     * Check if s is a substitution to specified variable
     * @param s        string to looks for
     * @param varName  varName to look
     * @return         new value for variable, null if not variable
     */
    private static String checkVariable(String s, String varName) {
        if ( !s.startsWith("#" + varName + "=") ) return null;
        String val = s.substring(varName.length() + 2).trim();
        val = val.replace("\"", "");
        val = val.replace("'", "");
        return val;
    }

    /**
     * Check if s is a substitution to specified variable.  Retain Quotes
     * @param s        string to looks for
     * @param varName  varName to look
     * @return         new value for variable, null if not variable
     */
    private static String checkVariableQ(String s, String varName) {
        if ( !s.startsWith("#" + varName + "=") ) return null;
        String val = s.substring(varName.length() + 2).trim();
        return val;
    }

    /**
     * Gets the name of the output file with complete path.
     * @param inFileName Name of the input (source) file
     * @return Output file name
     */
    public String getOutFileName(String inFileName) {
        try {
            File file = new File(inFileName);
            String name = file.getName();
            String dir = removeAllAfter(file.getCanonicalPath().toLowerCase(), name.toLowerCase());

            if ( directory != null && directory.length() > 0 ) {
                File outdir = new File(directory);
                if ( outdir.isAbsolute() ) dir = directory;
                else dir = dir + directory;
            }

            String outFileName = addToName(name, "Test");
            File outfile = new File(dir, outFileName);
            return outfile.getCanonicalPath();
            
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Gets the name of the output project file with complete path.
     * @param inFileName Name of the input (source) file
     * @return Output file name
     */
    public String getOutProjectName(String inFileName) {
        try {
            File file = new File(inFileName);
            String name = file.getName();
            String dir = removeAllAfter(file.getCanonicalPath().toLowerCase(), name.toLowerCase());
            String subdir = justFilePart(name) + "Test";

            if ( directory != null && directory.length() > 0 ) {
                File outdir = new File(directory);
                if ( outdir.isAbsolute() ) dir = addPath(directory, subdir);
                else dir = addPath( addPath(dir, directory), subdir );
            }

            String outFileName = addToName(name, "Test");
            File outfile = new File(dir, outFileName);
            return outfile.getCanonicalPath();

        } catch (IOException ioe) {
            return null;
        }
    }
}
