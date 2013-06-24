package cc.jyu.fi.comtest.c;

import java.util.Arrays;
import java.io.IOException;
import java.io.PrintWriter;

import cc.jyu.fi.comtest.BaseComTestScanner;
import cc.jyu.fi.comtest.ComTestIni;
import cc.jyu.fi.comtest.utils.FileUtilities;
import cc.jyu.fi.comtest.utils.Strings;

import static cc.jyu.fi.comtest.ComTestSyntax.*;
import static cc.jyu.fi.comtest.utils.StringUtilities.*;

/**
 * Class to do the file scanning and output for test class for C
 */
public class ComTestScanner extends BaseComTestScanner
{
    private final Strings functions = new Strings();    

    /**
     * Constructs the new ComTestScanner from stringlist contents
     * 
     * @param ini configuration file
     * @param contents where to scan the comment tests
     */
    public ComTestScanner(ComTestIni ini, Strings contents) {
        super(ini, contents);
    }
    
    /**
     * This factory method is just for testing purposes
     * @return a new handler
     */
    public CommentCodeHandler createCommentCodeHandler() {
        return new CommentCodeHandler();
    }

    /**
     * Handles to ComTest-comment and returns the position of last handled line.
     * 
     * The main method is readCode and all others are just helper methods to handle the lines.
     * 
     * Precondition: inside JavaDoc comment
     * Postcondition: still inside JavaDoc comment
     */
    public class CommentCodeHandler extends BaseComTestScanner.CommentCodeHandler {
        /**
         * Handles normal lines and table lines from ComTest lines.
         * The line is first divided to "sentences" so that
         * ; { } are each one "sentence".  Then ===, ~~ is try to
         * find and in case of success the corresponding assert is generated.
         * If the line has at least on $ sign, all sentences are kept as
         * a template sentences.  Also after starting the template, all
         * lines are kept as template lines.
         * If generated assert-line does not include ; , then it is added.
         * @param line line to handle.
         *
         * @example
         * <pre name="test">
         *   TestHelpper t = new TestHelpper();
         *
         *   t.codeHandler.handleSentences("int a = 5;");
         *   t.initCmnds.get(1) === "int a = 5";
         *
         *   t.initCmnds.clear();
         *   t.codeHandler.handleSentences("int a = 5; int b = 7;");
         *   t.initCmnds.get(0) === "    ";
         *   t.initCmnds.get(1) === "int a = 5";
         *   t.initCmnds.get(2) === "; ";
         *   t.initCmnds.get(3) === "int b = 7";
         *   t.initCmnds.get(4) === "; ";
         *
         *   t.initCmnds.clear();
         *   t.codeHandler.handleSentences("a === 5;");
         *   t.initCmnds.get(1) === "assertEquals(\"From: test line: 1\", 5, a)";
         *
         *   t.initCmnds.clear();
         *   t.codeHandler.handleSentences("get(request,new String[]{\"str\",\"id\"},13,true) === 123;");
         *   t.initCmnds.get(1) === "assertEquals(\"From: test line: 1\", 123, get(request,new String[]{\"str\",\"id\"},13,true))";
         *
         *   t.initCmnds.clear();
         *   t.codeHandler.handleSentences("\"cat\" =R= \"c.*t\";"); // "cat" =R= "c.*t";
         *   t.initCmnds.get(1) === "{ String _l_=\"cat\",_r_=\"c.*t\"; if ( !_l_.matches(_r_) ) fail(\"From: test line: 1\" + \" does not match: [\"+ _l_ + \"] != [\" + _r_ + \"]\");}";
         *
         *   t.initCmnds.clear();
         *   t.codeHandler.handleSentences("a ~~~ 6.0;");
         *   t.initCmnds.get(1) === "assertEquals(\"From: test line: 1\", 6.0, a, 0.000001)";
         *
         *   t.initCmnds.clear();
         *   t.codeHandler.handleSentences("\"a$1\""); /// Not a template line
         *   t.initCmnds.get(1) === "\"a$1\"";
         *
         *   t.template.size() === 0;
         * </pre>
         *
         *
         * @example
         * <pre name="test">
         *   String s="$a"; s === "$b"; $
         *     $a | $b
         *    ---------
         *      1 | 1
         *
         *  $a; "a".matches("^a$") === false;  // does not generate code
         * </pre>
         *
         * @example
         * <pre name="test">
         *   String s=null; s === null;
         *   "a".matches("^a$") === true;  // generate code
         * </pre>
         *
         * @example
         * <pre name="test">
         * String s="$a"; s === "$b"; $;
         *     $a | $b
         *    ---------
         *      $ | $
         *   "a".matches("^a$") === true;  // this generates
         * </pre>
         */
        public void handleSentences(String line)
        {
            int p;
            String eq, tol, tolPostFix="";
            String s = line;
            boolean isTemplateLine = indexOfNotInQuotes(s,TEMPLATELINEMARKER) >= 0;

            boolean isRegExp = false;

            Strings commands = new Strings();
            commands.add(indent + "  ");
            Strings sentences = splitToSentences(s);
            if ( tablePrinted ) {
            	template.clear();
            	tablePrinted = false;
            	everyLineInits = false;
            	vars = null;
        	}
            for ( int i=0; i<sentences.size(); i++ ) {
                String st = sentences.get(i);
                isRegExp = false;

                if ( st.length() == 0 ) {
                	continue;
                }
                if ( rawMode ) {
                	s = removeFromBegining(st," ",1);
                } else {
                	s = removeFromBegining(st, " ");
                }
                if ( s.length() == 0 ) {
                	continue;
                }

                p = indexOfNotInQuotes(s,ALMOSTMARKER1);
                eq = ALMOSTMARKER1;
                tol = ", " + TOLERANCE;
                
                if (p < 0) { // ~~
                	p = indexOfNotInQuotes(s, ALMOSTMARKER2);
                	eq = ALMOSTMARKER2;
            	}
                
                if (p < 0) { // ===
                	p = indexOfNotInQuotes(s, EQUALSMARKER);
                	eq = EQUALSMARKER;
                	tol = "";
                	tolPostFix = "";
            	} 
                if (p < 0) { // =>
                	p = indexOfNotInQuotes(s, EQUALSMARKER2);
                	eq = EQUALSMARKER2;
            	}          
                if (p < 0) { // =R=
                	p = indexOfNotInQuotes(s, REGEXPMARKER1);
                	eq = REGEXPMARKER1;
                	isRegExp = true;
            	} 
                if (p < 0) { // =~ 
                	p = indexOfNotInQuotes(s, REGEXPMARKER2);
                	eq = REGEXPMARKER2;
                	isRegExp = true;
            	}
                if (p >= 0) {
                    String leftSide = s.substring(0, p).trim();
                    String rightSide = s.substring(p + eq.length()).trim();
                    String message = "\"From: " + className + " line: "+ LINENUMBERMARKER + "\"";
                    if (isRegExp) {
                      commands.add("{");
                      commands.add("char * _l_ = " + leftSide + ";");
                      commands.add("char * _r_ = " + rightSide + ";");
                      commands.add("if (strcmp(_l_, _r_) != 0) {");
                      // FIXME: use strstr() to create the error message
                      //commands.add("    fail(\"" + message + " does not match: '" + _l_ + "' != '" + _r_ + "'\");");
                      commands.add("}");
                    } else {
                      commands.add("assertEquals" + tolPostFix + "(" + message + ", "+rightSide + ", " + leftSide + tol + ")");
                      checkSemiColon(sentences, i, commands);
                    }
                    continue;
                }
                commands.add(s);
            }
            commands.addAll(0, startOfLine);
            commands.add(endOfLine);
            if (isTemplateLine || template.size() > 0 ) {
            	template.add(commands);
            } else if ( oneTablePrinted || rawMode ) {
                outLines(commands,lineNr+1);
            } else {
                addInit(commands,lineNr+1);
            }
        }


        /**
         * Creates a header and footer for new ComTest test method.
         * If the name is for raw-code, then no method header is done.
         * If there is no name for the test method, then next method is
         * searched and that method name is used as a test method name.
         * @param s line to start the test.
         */
        protected void createHeaderAndFooter(String s) {
            int p = s.indexOf(STARTOFTEST);
            if ( p < 0 ) return;
            if ( s.indexOf("JAVA") > 0 ) { // start Raw mode
                methodName = "";
                methodHeader.add("\n");
                methodHeader.add(javaShortComment + COMTESTID + " " + COMTESTBEGIN + "");
                methodFooter.add(javaShortComment + COMTESTID + " " + COMTESTEND + "\n");
                rawMode = true;
                initLine = lineNr+1;
                baseIndent = "";
                return;
            }
            methodName = TEST + s.substring(p+STARTOFTEST.length());
            methodName = removeAllAfter(methodName, "\"");
            if ( methodName.equals(TEST)) methodName = tryToFindMethodName(contents,lineNr);
            methodHeader.add("\n");
            methodHeader.add("\n");
            methodHeader.add(javaShortComment + COMTESTID+ " " + COMTESTBEGIN + "\n");
            if ( methodThrows.length() == 0 ) // generate simple comment
              methodHeader.add("  /** " +methodName + METHODINDEXMARKER +" */\n");
            else { // generate better comments for @throws
                methodHeader.add("  /** \n");
                methodHeader.add("   * " + methodName + METHODINDEXMARKER +" \n");
                String exception = remove(methodThrows, "throws");
                Strings excptions = splitBy(exception, ",");
                for (String exp:excptions)
                  methodHeader.add("   * @throws " + exp.trim() +" when error\n");
                methodHeader.add("   */\n");

            }
            //methodHeader.add("  @Test\n");
            methodHeader.add("  public: void "+methodName + METHODINDEXMARKER +"() " + methodThrows + "{");
            //methodHeader.add("  @Test // " + COMTESTID+ " " + COMTESTBEGIN + "\n");
            //methodHeader.add("  public void "+methodName + METHODINDEXMARKER +"() {");
            methodFooter.add("  } // " + COMTESTID + " " + COMTESTEND + "\n");
            initLine = lineNr+1;
            functions.add(methodName);
        }

        /**
         * Check if line is THROWS line.  If it is, correct
         * the start and end lists as needed.
         * @param s line to look.
         * @return line without THROWS sentence
         * <pre name="test">
         *   TestHelpper t = new TestHelpper("test");
         *
         *   t.scan("a = 5; #THROWS IndexOutOfBoundsException");
         *   t.out[1] === "    try {";
         *   t.out[2] === "    a = 5; ";
         *   t.out[3] =R= "    fail\\(.* Did not throw IndexOutOfBoundsException\"\\);";
         *   t.out[4] === "    } catch(IndexOutOfBoundsException _e_){ _e_.getMessage(); }";
         *
         *   t.scan("a = 5; #THROWS (IndexOutOfBoundsException ex) { ex.getMessage() =R= \".*Index.*\"; }");
         *   t.out[3] =R= "    fail\\(.* Did not throw \\(IndexOutOfBoundsException ex\\)\"\\);";
         *   t.out[4] =R= "    \\} catch\\(IndexOutOfBoundsException ex\\) \\{ \\{.*matches.*\\}; \\}";
         * </pre>
         *
         */
        protected String checkThrows(String s) {
            int p = indexOfNotInQuotes(s, THROWSMARKER);
            if ( p < 0 ) return s;
            indent = baseIndent + "";
            String sentence = s.substring(0,p);
            String exception = s.substring(p+THROWSMARKER.length()).trim();
            String exceptionBeg = exception;
            p = exceptionBeg.indexOf(")");
            if ( p >= 0 ) exceptionBeg = exceptionBeg.substring(0,p+1);
            startOfLine.add(0,baseIndent + "  try {\n");
            endOfLine.add(0,"\n"+indent + "  fail(\"" + className + ": " + (lineNr+1) +" Did not throw " + exceptionBeg + "\");\n");
            if ( exception.startsWith("(") ) {
               String line = TestHelpper.scanToLine(exception);
               endOfLine.add(1,baseIndent + "  } catch" + line);
            }
            else
              endOfLine.add(1,baseIndent + "  } catch(" + exception + " _e_){ _e_.getMessage(); }");
            return sentence;
        }
    }

    private Strings getBeforeTest() {
        return FileUtilities.readResourceFile("cpp/comtestAsserts.h", "UTF-16");
    }

    /**
     * Reads possible old file and removes all ComTest generated code
     * and create the output to outlines
     * @throws IOException if outPutFile is not possible to open
     */
    protected void createTestFileBegin() throws IOException {
        String packageline = "";
        Strings testContents = new Strings();
        
        outlines = new Strings();

        deleteComTestLines(testContents, outlines);
        if (packageName.length() > 0) {
            String pack = packageName;
            if (".".equals(pack)) {
            	pack = originalPackageName;
            } else if (pack.charAt(0) == '.') {
                pack = originalPackageName + "." + pack.substring(1);
            }
            if (originalPackageName.length() == 0) {
            	pack = "";
            }
            packageline = "package " + pack + ";";
            testFilePackageName = pack;
        }

        if (! ini.noImportAtAll) {
            String[] jimp = JUNITIMPORTS.split(":");
            imports.addAll(Arrays.asList(jimp));
        }

        if (outlines.size() > 2) {
        	return;
        }

        if (testFilePackageName.length() > 0) {
        	addOutln(packageline);
        }
        addOutln("");
        addOutln("/**");
        addOutln(" * Test class made by ComTest" + getComTestVersion());
        addOutln(" */");
    }


    /**
     * Creates the inner part of test class
     */
    protected void createTestFileInner()
    {
        boolean javaComment=false;
        
        for (int i=0; i < contents.size(); i++ ) {
            String s = contents.get(i).trim();
            if (ComTestIni.checkVariables(s)) {
            	continue;
            }
            
            if (javaComment && indexOfNotInQuotes(s, "*/")>=0 ) {
                javaComment = false;
                continue;
            }
            
            if (! javaComment) {
                int isc = indexOfNotInQuotes(s, "/*");
                int iec = indexOfNotInQuotes(s, "*/", QUOTES, isc + 1);
                if (isc >= 0 && iec < 0) {
                	javaComment = true;
                }
                continue;
            }
            
            if (s.indexOf(STARTOFTEST) >= 0) {
                CommentCodeHandler codeHandler = new CommentCodeHandler();
                int lastLine = codeHandler.readCode(i);
                if (lastLine != i) {
                	i = lastLine-1;
                }
            }
        }
    }

    protected void addImports() {
    }

    /**
     * For C files, put the original file first
     */
    @Override
    protected void addHeader(PrintWriter out)
    {
        println(out, contents, "\n", " main(", " " + testClassName + "OrigMain(");
        Strings beforeTest = getBeforeTest();
        println(out, beforeTest, "\n"); // next put something before test
    }
    
    /**
     * Creates the end of test class
     */
    protected void createTestFileEnd()
    {
        addOutln("");
        addOutln("  int runner(void) {");
        addOutln("      int errors = 0;");
        for (String s:functions) {
          addOutln("      try { "+s+"(); } catch (...) { errors++; }");
        }
        addOutln("      if ( errors == 0 ) std::cout << \"ok\" << std::endl;");
        addOutln("      return errors;");
        addOutln("  }");
        addOutln("");
        addOutln("};");
        addOutln("");
        addOutln("int main(void) {");
        addOutln("    " + testClassName + " t; t.runner();");
        addOutln("    return 0;");
        addOutln("}");
    }


}
