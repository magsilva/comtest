package cc.jyu.fi.comtest;

import cc.jyu.fi.comtest.utils.Strings;

import java.util.Calendar;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import static cc.jyu.fi.comtest.ComTestSyntax.*;
import static cc.jyu.fi.comtest.utils.StringUtilities.*;

/**
 * Abstract base class to do the file scanning and output for test class
 * @author tojukarp
 */
public abstract class BaseComTestScanner {
    protected static final String JAVAIMPORT = "import ";
    protected final Strings contents;
    protected final Strings imports = new Strings();
    protected Strings outlines = null;
    protected boolean needsStaticImport = false;
    protected boolean needsClassImport = false;
    protected String className = null;
    protected String outFileName = null;
    protected String outDirName  = null;
    protected String testClassName = null;
    protected String packageName = "";
    protected String originalPackageName = "";
    protected String testFilePackageName = "";
    //protected String directory = null;
    protected int printedTableLines = 0;
    protected ComTestIni ini;

    /**
     * Constructs the new ComTestScanner from stringlist contents
     * @param ini configuration file
     * @param contents where to scan the comment tests
     */
    public BaseComTestScanner(ComTestIni ini, Strings contents) {
        this.ini = ini;
        this.contents = contents;
        outlines = new Strings();
    }

    public Strings getImports() { return imports; }

    /**
     * This method is just for testing purposes to return
     * current outlines.
     * @return current outlines.
     */
    public Strings getOutlines() {
        return outlines;
    }

    /**
     * This factory method is just for testing purposes
     * @return a new handler
     */
    public abstract CommentCodeHandler createCommentCodeHandler();

    /**
     * Add string s to outlines
     * @param s
     */
    protected void addOut(String s) {
        outlines.add(s);
    }

    /**
     * Add string s to outlines with new line
     * @param s
     */
    protected void addOutln(String s) {
        outlines.add(s+"\n");
    }

    /**
     * Add strings from list to outlines by separating from new line
     * @param list strings to add
     */
    protected void addOut(Strings list) {
        for (String s:list) addOut(s);
    }

    /**
     * Adds the stringlist by replacing target-strings by replacement strings
     * Separator is not printed after last line
     * @param list    stringlist to print
     * @param sep     string to separate items
     * @param target  string that is replaced
     * @param replacement string by what to replace
     */
    protected void addOut(Strings list, String sep, String target, String replacement) {
        if ( list.size() == 0 ) return;
        for (int i=0; i<list.size();i++)
            addOut(list.get(i).replace(target, replacement)+sep);
    }

    /**
     * Deletes from the list all lines that contains target
     * outside of quotes and comments
     * @param list   where to delete
     * @param target what is condition for delete
     * @return the list itself
     */
    private Strings delete(Strings list, String target, String quotes) {
       int ir,iw = 0;
       for (ir=0; ir<list.size(); ir++) {
           String s = list.get(ir);
           int p = indexOfNotInQuotes(s, target, quotes);
           int pc = indexOfNotInQuotes(s, "//", quotes);
           // int p = s.indexOf(target);
           if ( p < 0 || ( 0 <= pc  && pc < p ) ) list.set(iw++, s);
       }
       if ( ir != iw ) list.removeRange(iw,ir);
       return list;
    }

    /**
     * Prints the stringlist
     * Separator is not printed after last line
     * @param list    stringlist to print
     * @param target  string that is replaced
     * @param replacement string by what to replace
     */
    protected void addOut(Strings list, String target, String replacement) {
        addOut(list,"",target,replacement);
    }

    /**
     * Adds imports from configuration file.
     */
    protected void getIniImports() {
        if ( ini.dynamicImport ) {
            imports.add(JAVAIMPORT + originalPackageName + "." + className + ".*;");
        }
        if ( ini.classImport ) {
            imports.add(JAVAIMPORT + originalPackageName + "." + className +";");
        }
        if ( ini.packageImport ) {
            imports.add(JAVAIMPORT + originalPackageName + ".*;");
        }

        imports.add(JAVAIMPORT, ini.imports, "");
    }

    /**
     * Prints the stringlist by replacing target-strings by replacement strings
     * @param out     stream to print
     * @param list    stringlist to print
     * @param sep     string to separate items
     */
    protected void println(PrintWriter out, Strings list, String sep) {
        for (String s:list) out.print(s+sep);
    }

    /**
     * Prints the stringlist by replacing target-strings by replacement strings
     * @param out     stream to print
     * @param list    stringlist to print
     * @param sep     string to separate items
     * @param target  string that is replaced
     * @param replacement string by what to replace
     */
    protected void println(PrintWriter out, Strings list, String sep, String target, String replacement) {
        for (String s:list) out.print(s.replace(target, replacement)+sep);
    }

    /**
     * Returns integer i by at least two characters
     * @param i
     * @return i by at least two characters
     */
    protected static String two(int i) {
        if ( i >= 10 || i < 0 ) return "" + i;
        return "0" + i;
    }


    /**
     * Returns version string for class comments
     */
    public static String getComTestVersion() {
        Calendar cal = Calendar.getInstance();
        String timestamp = ""+cal.get(Calendar.YEAR) + "." + two(cal.get(Calendar.MONTH)+1) + "." +
                           two(cal.get(Calendar.DAY_OF_MONTH)) + " " +
                           two(cal.get(Calendar.HOUR_OF_DAY)) + ":" + two(cal.get(Calendar.MINUTE)) + ":" +
                           two(cal.get(Calendar.SECOND));
        return "@version " + timestamp + " // "+ COMTESTID;
    }

    /**
     * Scans String list and deletes all lines written by ComTest
     * @param testContents list to scan
     * @param outlines list where is the result (all lines with \n)
     */
    protected static void deleteComTestLines(Strings testContents,Strings outlines) { // NOPMD by vesal on 13.1.2008 15:30
        if ( testContents == null ) return;
        int blankLines = 0;
        boolean inOwnWrite = false;
        for (String s : testContents ) {
            if ( ( indexOfNotInQuotes(s, COMTESTID) >= 0 ) || ( indexOfNotInQuotes(s, COMTESTIDOLD) >= 0 ) ) {
                if ( indexOfNotInQuotes(s,"@version") >= 0 ) outlines.add(" * " + getComTestVersion()+"\n");
                else if ( indexOfNotInQuotes(s, COMTESTBEGIN) >= 0 ) inOwnWrite = true;
                else if ( indexOfNotInQuotes(s, COMTESTEND) >= 0 ) inOwnWrite = false;
                continue;
            }
            if ( inOwnWrite ) continue;

            if ( s.trim().length() == 0 ) {
                blankLines++;
                if ( blankLines >= 2) continue;
            } else blankLines = 0;

            outlines.add(s+"\n");

        }

        // Delete last closing }
        for ( int ir = outlines.size()-1; ir >= 0; ir--) {
           String s = outlines.get(ir);
           if ( indexOfNotInQuotes(s, "}") >= 0 ) {
               outlines.removeRange(ir,outlines.size());
               return;
           }
        }
    }

    /**
     * May change outFileName to same directory, if no package
     * @param fileName name to use as a base name
     * @throws IOException
     */
    protected void checkFileNameOnceAgain(String fileName) throws IOException  {
        if ( testFilePackageName.length() > 0 ) return;
        //directory = "./";
        findNames(fileName);
    }

    /**
     * Find outName and className from fileName
     * @param fileName name to look;
     * @return true
     * @throws IOException when problems with the file
     */
    public boolean findNames(String fileName) throws IOException {
        File file = new File(fileName);
        String name = file.getName();
        String dir = removeAllAfter(file.getCanonicalPath(),name);

        if ( ini.directory != null && ini.directory.length() > 0 ) {
            File outdir = new File(ini.directory);
            if ( outdir.isAbsolute() ) dir = ini.directory;
            else dir = dir + ini.directory;
        }
        className = justFilePart(name);
        outFileName = addToName(name,"Test");
        testClassName = justFilePart(outFileName);
        File outfile = new File(dir,outFileName);
        outDirName = dir;
        outFileName = outfile.getCanonicalPath();
        return true;
    }

    /**
     * Finds the package line from contents.
     * Substitutes to packageName
     * @param contents where to find
     * @return true if found
     */
    protected boolean findPackage(Strings contents) {
        String pack;
        for (String s:contents) {
            if ( indexOfNotInQuotes(s, "package") >= 0 ) {
                pack = removeFromBegining(s,"package").trim();
                pack = removeAllAfter(pack,";").trim();
                if ( isEmpty(ini.packageName) ) packageName = pack;
                else packageName = ini.packageName;
                originalPackageName = pack;
                return true;
            }
        }
        return false;
    }

    /**
    * Adds the line s+"\n" to outlines if it is not there already.
    * The line "is there" if trim of it starts with s
    * @param i place to add if not already there
    * @param s line to add if not found
    * @return next place to add so i+1 if added, i if not
    */
   protected int addIfNotAllready(int i,String s) {
       for ( String st:outlines )
           if ( st.trim().startsWith(s ) ) return i;
       outlines.add(i,s+"\n");
       return i+1;
   }

    protected abstract void addImports();

    protected void addHeader(PrintWriter out) {
    }

    protected abstract void createTestFileBegin() throws IOException;
    protected abstract void createTestFileInner();
    protected abstract void createTestFileEnd();

    /**
     * Scan the contents and make a test class
     * @param fileName filename read to contents
     * @return outName if ok, null if something wrong
     */
    public String scan(String fileName) {
        PrintWriter out = null;
        boolean status = false;
        try {
            findNames(fileName);
            findPackage(contents);
            getIniImports();

            createTestFileBegin();
            createTestFileInner();
            createTestFileEnd();
            addImports();

            //checkFileNameOnceAgain(fileName);

            status = new File(outDirName).mkdirs();
            out = new PrintWriter(new FileWriter(outFileName));
            addHeader(out);
            println(out,outlines,"");
            return outFileName;
        } catch (IOException e) {
            String s = "";
            if ( !status ) s = "\nNot able to make dir: " + outDirName;
            System.err.println(fileName +  " => " + outFileName + " " + e.getMessage() + s);
            return null;
        } finally {
            if ( out != null ) out.close();
        }
    }

    /**
     * Handles to ComTest-comment and returns the position
     * of last handled line.
     * The main method is readCode and all others are just helper
     * methods to handle the lines.
     * Precondition: inside JavaDoc comment
     * Postcondition: still inside JavaDoc comment
     */
    public abstract class CommentCodeHandler {
        protected static final String javaShortComment = "  // ";
        protected int lineNr;
        protected String methodName = null;
        protected final Strings template = new Strings();
        protected final Strings initCmds = new Strings();
        protected final Strings methodHeader = new Strings();
        protected final Strings methodFooter = new Strings();
        protected boolean methodHeaderPrinted = false;
        protected int methodIndex = 1;
        protected String sMethodIndex = "";
        protected Strings vars = null;
        protected Strings values = null;
        protected int initLine = 0;
        protected boolean everyLineInits = false;
        protected boolean tablePrinted = false;
        protected boolean oneTablePrinted = false;
        protected boolean rawMode = false;
        protected String baseIndent = "  ";
        protected String indent = baseIndent;
        protected final Strings startOfLine = new Strings();
        protected final Strings endOfLine = new Strings();
        protected String methodThrows = "";

        public Strings getTemplate() { return template; }

        /**
         * Juss helper method for tests
         * @param s
         */
        public void setMethodName(String s) {
            methodName = s;
        }

        /**
         * This is just for testing purposes to return the initCmds
         * @return initCmnds
         */
        public Strings getInitCmnds() {
            return initCmds;
        }


        /**
         * Try to find name for next method.  If no name is found, then
         * a sequence number is used.
         * This is very unfinished and works only for nice code
         * where the method name and type is on the same line.
         * @param contents where to find the name
         * @param lineNr where to start
         * @return test method name
         * @example
         * <pre name="test">
         *   TestHelpper t = new TestHelpper();
         *   Strings s1 = new Strings();
         *   s1.add("*" + "/");
         *   s1.add("public int function()");
         *   t.codeHandler.tryToFindMethodName(s1,0) === "testFunction1";
         *   s1 = new Strings();
         *   s1.add("*" + "/");
         *   s1.add("@" + "example public int AnotherFunction (int n, double d) // comment ");
         *   t.codeHandler.tryToFindMethodName(s1,0) === "testAnotherFunction1";
         *   s1 = new Strings();
         *   s1.add("*" + "/");
         *   s1.add("public ClassName (int n)");
         *   t.codeHandler.tryToFindMethodName(s1,0) === "testClassName1";
         *   s1 = new Strings();
         *   s1.add("*" + "/");
         *   s1.add("public AnotherClassName()");
         *   t.codeHandler.tryToFindMethodName(s1,0) === "testAnotherClassName1";
         * </pre>
         */
        public String tryToFindMethodName(Strings contents, int lineNr) { // NOPMD by vesal on 13.1.2008 15:29
            String autoNumber = ""+(lineNr+1);
            String name;
            String type="";
            boolean mayneedStatic = false;
            boolean isClass = false;
            boolean inComment = true;
            for ( int i=lineNr; i<contents.size(); i++ ) {
                StringBuilder sb = new StringBuilder(contents.get(i));
                removeAllAfter(sb, "//"); trim(sb);
                if ( sb.length() == 0 ) continue;
                if ( indexOfNotInQuotes(sb,"/*") >= 0 ) { inComment = true; continue; }
                if ( indexOfNotInQuotes(sb,"*/") >= 0 ) { inComment = false; continue; }
                if ( inComment ) continue;
                remove(sb,"public");
                remove(sb,"protected");
                remove(sb,"private");
                if ( remove(sb,"static") ) mayneedStatic = true;
                remove(sb,"final");
                if ( remove(sb,"class") ) isClass = true;
                removeAllAfter(sb,"extends");
                removeAllAfter(sb,"implements");
                String s = sb.toString();
                s = s.replaceAll("@.[^\\s]*\\s*",""); // Remove annotations
                s = s.replaceAll("\\s*\\(","(");        // Remove all spaces before (
                s = s.trim();
                if ( s.length() == 0 ) continue;
                int p  = s.indexOf(' ');
                int pb = s.indexOf('(');
                if ( pb < p ) p = -1; // public ClassName() case;
                if ( p >= 0 ) { type = s.substring(0,p); s = s.substring(p+1); }
                int ic;
                for ( ic=0; ic<s.length(); ic++)
                    if ( !Character.isJavaIdentifierPart(s.charAt(ic)) ) break;
                name = s.substring(0,ic);
                if ( name.length() == 0 ) name = type;
                if ( name.length() == 0 ) return TEST + autoNumber;
                name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                if ( !isClass &&  mayneedStatic ) needsStaticImport = true;
                else needsClassImport = true;
                return TEST + name + autoNumber;
            }
            return TEST + autoNumber;
        }

        /**
         * Check if line is THROWS line.  If it is, correct
         * the start and end lists as needed.
         * @param s line to look.
         * @return line without THROWS sentence
         */
        protected String checkMethodThrows(String s) {
            int p = indexOfNotInQuotes(s, THROWSMARKER);
            if ( p < 0 ) return s;
            String sentence = remove(s.substring(0,p),"*").trim();
            if ( sentence.length() > 0 ) return s;
            String exception = s.substring(p+THROWSMARKER.length()).trim();
            methodThrows = "throws " + exception + " ";
            return sentence;
        }


        /**
         * Find comments and put possible try catch to start and end
         * of the the current line's start and end lists.
         * @param s line to handle
         * @return the line where comments and THROWS-sentence is removed.
         */
        protected String saveStartAndEndOfLine(String s) {
            startOfLine.clear();
            endOfLine.clear();
            indent = baseIndent;
            int p = indexOfNotInQuotes(s, "//");
            if ( p < 0 ) {
              endOfLine.add("\n");
              return checkThrows(s);
            }
            endOfLine.add(" " + s.substring(p) + "\n");
            return checkThrows(s.substring(0,p));
        }


        /**
         * Copies method footer to outlines if method header is
         * already moved.  Nothing is done if method header
         * is not copied.
         * @param realLineNr current line number
         */
        protected void outFooter(int realLineNr) {
            if ( methodHeaderPrinted ) {
                addOut(methodFooter);
                if ( realLineNr > 0 ) initLine = realLineNr;
            }
            methodHeaderPrinted = false;
        }


        /**
         * Moves header lines to outlines.  Possible method
         * index markers are replaced by method index.
         */
        public void outNewHeader() {
            outFooter(0);
            addOut(methodHeader, METHODINDEXMARKER, sMethodIndex);
            methodIndex++;
            sMethodIndex = ""+methodIndex;
            methodHeaderPrinted = true;
            if ( initLine == 0 ) initLine = lineNr+1;
            addOutln(baseIndent + javaShortComment + className + ": " + initLine);
            addOut(initCmds);
        }

        /**
         * Moves list to outlines.  If header is not moved,
         * then move it now. Possible target strings are replaced
         * by replacement strings while moving.
         * @param list what to move
         * @param target what is replaced
         * @param replacement by what to replace
         */
        protected void outWithHeader(Strings list, String target, String replacement) {
            if ( !methodHeaderPrinted ) outNewHeader();
            addOut(list,target,replacement);
        }


        /**
         * Add sentences to init section.  Possible line number markers
         * are replaced by current line number.
         * @param commands sentences to add
         * @param realLineNr real line number for current line
         */
        protected void addInit(Strings commands, int realLineNr) {
            if ( initLine == 0 ) initLine = realLineNr;
            initCmds.add(commands.replace(LINENUMBERMARKER, ""+realLineNr));
        }


        /**
         * Moves commands to outlines.  If header is not moved,
         * then move it now. Possible line number markers
         * are replaced by current line number.
         * @param commands what to move
         * @param realLineNr real line number for current line
         */
        protected void outLines(Strings commands, int realLineNr) {
            outWithHeader(commands,LINENUMBERMARKER, ""+realLineNr);
        }


        /**
         * Check if s is one of the JUnit annotation.
         * @param s line to look
         * @return true if annotation, false otherwise.
         */
        protected boolean jUnitAnnotate(String s) {
            if ( s.startsWith("@Test") )   return true;
            if ( s.startsWith("@Before") ) return true;
            if ( s.startsWith("@After") )  return true;
            return false;
        }


        /**
         * Annotations in comments must be prefixed with something.
         * This takes the prefixing away.
         * @param s where to look annotation
         * @return s if no annotation, otherwise annotation with
         *           prefix info removed.
         * @example
         * <pre name="test">
         *   TestHelpper t = new TestHelpper();
         *   t.codeHandler.handleAnnotationInComments("a") === "a";
         *   t.codeHandler.handleAnnotationInComments("* #@Test") === "@Test";
         *   t.codeHandler.handleAnnotationInComments("* #@example") === "* #@example";
         *
         * </pre>
         */
        public String handleAnnotationInComments(String s) {
            int p = indexOfNotInQuotes(s, "@");
            if ( p < 0 ) return s;
            String st = s.substring(p);
            if ( jUnitAnnotate(st) ) return st;
            return s;
        }

        /**
         * Deletes \n from the last line of cmds
         * @param cmds where to delete
         */
        protected void deleteLastNewLine(Strings cmds) {
            if ( cmds.size() <= 0 ) return;
            String s = cmds.get(cmds.size()-1);
            if ( s.length() == 0 ) return;
            if ( s.charAt(s.length()-1) != '\n' ) return;
            cmds.set(cmds.size()-1,s.substring(0,s.length()-1)+" ");
        }

        /**
         * Check if the line is a table line, so containing either " | "
         * or | at the beginning or end.
         * @param s line to check
         * @return true if was a table line.
         * @example
         * <pre name="test">
         *   TestHelpper t = new TestHelpper();
         *   t.codeHandler.isTableLine("") === false;
         *   t.codeHandler.isTableLine("a") === false;
         *   t.codeHandler.isTableLine("a|") === true;
         *   t.codeHandler.isTableLine("|a") === true;
         *   t.codeHandler.isTableLine(" a|b ") === false;
         *   t.codeHandler.isTableLine(" a | b ") === true;
         * </pre>
         */
        public boolean isTableLine(String s) {
            if ( indexOfNotInQuotes(s,COLUMNSEPARATOR) >= 0 ) return true;
            if ( s.startsWith(COLUMNCHAR) ) return true;
            if ( s.endsWith(COLUMNCHAR) ) return true;
            return false;
        }

        /**
         * Handles lines like     "  $s        |  $what |  $result"
         * or                     " "aab"      |  "a"   |  "ab"  "
         * If there is a $ on the line, it is kept as a header line.
         * Otherwise as a normal values line.  From header line
         * the variable names a collected.  In normal line the
         * variable values a replaced in the corresponding template
         * line.  Sentences with unused variable names are removed.
         * @param line line to handle.
         */
        protected void handleTableLine(String line) { // NOPMD by vesal on 13.1.2008 15:29
            String s = line;
            if ( template.size() == 0 ) return; // No use of table if no template
            if ( s.startsWith(COLUMNCHAR) ) s = s.substring(1);
            if ( s.endsWith(COLUMNCHAR) ) s = s.substring(1,s.length()-1);
            if ( vars == null ) { // || indexOfNotInQuotes(s, TEMPLATELINEMARKER ) >= 0 ) {
                vars = splitBy(s,COLUMNSEPARATOR,COLUMNSEPARATOR.length(),true);
                return;
            }
            values = splitBy(s,COLUMNSEPARATOR,COLUMNSEPARATOR.length(),true);
            Strings cmds = template.clone();
            for (int v=0; v<values.size(); v++ ) {
                if ( v >= vars.size() ) break;
                String value = values.get(v).trim();
                String var = vars.get(v).trim();
                if ( value.equals(DELETESENTENCEMARKER) ) delete(cmds,var,"");
                else cmds.replace(var, value);
            }
            if ( everyLineInits ) outFooter(lineNr+1);
            delete(cmds, "$", QUOTES);  // remove sentences with $ outside ""
            deleteLastNewLine(cmds);
            cmds.addAll(0, startOfLine);
            cmds.add(endOfLine);
            outWithHeader(cmds,LINENUMBERMARKER, ""+(lineNr+1));
            printedTableLines += cmds.size();

            if ( everyLineInits )
                outFooter(lineNr+1);
            tablePrinted = true;
            oneTablePrinted = true;
        }


        /**
         * Check if there is coming a ; then nothing is done.
         * If there is no ; coming, then one ; is added to commands.
         * @param sentences where to look ;
         * @param i from what index to look ;
         * @param commands where to add ; if needed
         */
        protected void checkSemiColon(Strings sentences, int i, Strings commands) {
            if ( ( i < sentences.size() - 1 ) &&
                 ( sentences.get(i+1).indexOf(";") >= 0 ) ) return;
            commands.add(";");
        }


        /**
         * Check if line is pseudo comment /.* or  *./
         * @return comments handled or s
         */
        protected String isPseudoComment(String line) {
            String s = line;
            s = s.replaceAll("\\/\\.\\*", "/*");
            s = s.replaceAll("\\*\\.\\/", "*/");
            return s;
        }

        /**
         * Reads the code from contents starting from line start
         * and returns the line last handled.
         * The ComTest block ends to next @-line or
         * to end of pre-tag.
         * @param start first line index to handle
         * @return last line index handled
         */
        public int readCode(int start) { // NOPMD by vesal on 13.1.2008 15:30
            methodThrows = "";
            printedTableLines = 0;
            try {
                for ( lineNr = start; lineNr < contents.size(); lineNr++ ) {
                    String s = contents.get(lineNr);
                    s = saveStartAndEndOfLine(s);
                    if ( s.indexOf("*/") >= 0 ) return lineNr;
                    if ( s.indexOf("</pre>") >= 0 ) return lineNr;
                    s = removeFromBeginingTrim(s,"*");
                    if ( lineNr == start ) s = s.replaceAll("@.[^\\s]*\\s*",""); // Remove annotations from 1st line
                    if ( !rawMode ) s = s.trim(); // in raw-mode save spaces
                    if ( s.length() <= 0 ) {
                        if ( rawMode ) // In raw-mode empty lines preserved
                            outLines(endOfLine, lineNr+1);
                        continue;
                    }
                    s = isPseudoComment(s);

                    if ( ( s.charAt(0)== '@' ) && !jUnitAnnotate(s) ) return lineNr;
                    s = handleAnnotationInComments(s);
                    if ( ComTestIni.checkVariables(s)) continue;

                    if ( s.indexOf(STARTOFTEST) >= 0 ) {
                        checkMethodThrows(contents.get(lineNr+1));
                        createHeaderAndFooter(s);
                        continue;
                    }

                    if ( methodName == null ) continue;

                    if ( indexOfNotInQuotes(s, SEPARATORLINE) >= 0 ) continue;

                    if ( indexOfNotInQuotes(s, INITLINE) >= 0 ) { // new init
                        if ( vars == null && EVERYLINEINITS1.equals(INITLINE) ) everyLineInits = true;
                        else outFooter(lineNr+1);
                        continue;
                    }

                    if ( indexOfNotInQuotes(s, EVERYLINEINITS1) >= 0 ||
                         indexOfNotInQuotes(s, EVERYLINEINITS2) >= 0 ) {
                        everyLineInits = true;
                        continue;
                    }

                    if ( isTableLine(s) ) { handleTableLine(s); continue;  }

                    handleSentences(s);
                }
            } finally {
                if ( !methodHeaderPrinted && initCmds.size() > 0 ) outNewHeader();
                    if ( template.size() > 0 && printedTableLines == 0 ) {
                    if ( !methodHeaderPrinted  ) outNewHeader();
                    addOut(template,LINENUMBERMARKER,""+(lineNr+1));
                    }
                outFooter(0);
            }
            return lineNr;
        }

        protected abstract void createHeaderAndFooter(String s);
        protected abstract String checkThrows(String s);
        public abstract void handleSentences(String line);
    }
}
