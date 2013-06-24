// ComTest - Comments for testing
package comtest;

import comtest.utils.Strings;
import static comtest.utils.StringUtilities.*;

/**
 * ComTest syntax constants and methods.
 * @author tojukarp
 */
public abstract class ComTestSyntax {
    // #STATICIMPORT
    // #import comtest.utils.Strings;

    public static final String JAVAIMPORT = "import ";
    public static final String TEST = "test";

    public static final String SEPARATORLINE        = "-----";
    public static final String MACROMARKER          = "#";
    public static final String LINENUMBERMARKER     = "#LINE#";
    public static final String METHODINDEXMARKER    = "#METHODINDEX#";
    public static final String STARTOFTEST          = "<pre name=\"" + TEST;
    public static final String COMTESTID            = "Generated by ComTest";
    public static final String COMTESTIDOLD         = "Genereted by ComTest";
    public static final String COMTESTBEGIN         = "BEGIN";
    public static final String COMTESTEND           = "END";
    public static final String STATICIMPORT         = "#STATICIMPORT";
    public static final String DYNAMICIMPORT        = "#DYNAMICIMPORT";
    public static final String CLASSIMPORT          = "#CLASSIMPORT";
    public static final String PACKAGEIMPORT        = "#PACKAGEIMPORT";
    public static final String NOIMPORT             = "#NOIMPORT";
    public static final String COMTESTINI           = "ComTest.ini";

    private static final String equalsTest = "Assert.AreEqual(%s, %s, \"%s\");";
    private static final String almostTest = "Assert.AreEqual(%s, %s, %s, \"%s\");";
    private static final String regexTest = "Assert.IsTrue( System.Text.RegularExpressions.Regex.Matches(%s, %s).Count > 0, \"%s\" );";

    public static final ComTestOperator EQUALS =
       new ComTestBinaryOperator(equalsTest, false, "===");
    public static final ComTestOperator ALMOST =
       new ComTestBinaryOperator(almostTest, false, "~~~", "~~");
    public static final ComTestOperator REGEX =
       new ComTestBinaryOperator(regexTest, true, "=R=", "=~");

    public static final String THROWSMACRO          = "throws";
    public static final String IMPORTMACRO          = "import";
    public static final String IMPORTMACRO2         = "using";

    // Constants that have corresponding variables in ComTestScanner
    public static String COLUMNSEPARATOR      = " | ";
    public static String COLUMNCHAR           = "|";
    public static String TEMPLATELINEMARKER   = "$";
    public static String DELETESENTENCEMARKER = "---";
    public static String INITLINE             = "=====";
    public static String EVERYLINEINITS1      = "=====";
    public static String EVERYLINEINITS2      = "=== Every line inits ===";
    public static String EQUALSMARKER         = "===";
    public static String EQUALSMARKER2        = "=>";
    public static String ALMOSTMARKER1        = "~~~";
    public static String ALMOSTMARKER2        = "~~";
    public static String REGEXPMARKER1        = "=R=";
    public static String REGEXPMARKER2        = "=~";
    public static String TOLERANCE            = "0.000001";
    public static String THROWSMARKER         = "#THROWS";
    public static String IMPORTMARKER         = "#import";
    public static String PACKAGEMARKER        = "#PACKAGE=";
    public static String DIRECTORYMARKER      = "#DIRECTORY=";
    public static String BEFORETESTCLASS      = "@SuppressWarnings({ \"PMD\" })";
    public static String JUNITIMPORTS         = "import static org.junit.Assert.*;:import org.junit.*;";

    /**
     * Split line to smaller tokens. Line is divided from
     * ;, { or }.  The splitter chars are returned as
     * own one char sentences.
     * @param s line to split
     * @return line that is split to smaller sentences.
     *
     * @example
     * <pre name="test">
     *   Strings sns = splitToSentences("a === 5;");
     *   sns.size() === 2;
     *   sns.get(0) === "a === 5"; sns.get(1) === "; ";
     *   sns.toString("|") === "a === 5|; ";
     *
     *   sns = splitToSentences("a === 5; b === 3;");
     *   sns.size() === 4;
     *   sns.toString("|") === "a === 5|; | b === 3|; ";
     *
     *   sns = splitToSentences("if ( a < 3 ) { a === 5; } else b === 3;");
     *   sns.size() === 8;
     *   sns.toString("|") === "if ( a < 3 ) |{ | a === 5|; |} |else | b === 3|; ";
     *
     *   sns = splitToSentences("if ( a < 3 ) { a === 5; } else { b === 3; }");
     *   sns.size() === 10;
     *   sns.toString("|") === "if ( a < 3 ) |{ | a === 5|; |} |else |{ | b === 3|; | }";
     *
     *   sns = splitToSentences("get(request,new String[]{\"str\",\"id\"},13,true) === 123;");
     *   sns.size() === 2;
     *   sns.toString("|") === "get(request,new String[]{\"str\",\"id\"},13,true) === 123|; ";
     * </pre>
     */
    public static Strings splitToSentences(String s) { // NOPMD by vesal on 13.1.2008 15:29
        Strings sentences = new Strings();
        String sentence,separator;
        int p = 0;
        while ( true ) {
            int p1 = indexOfNotInQuotes(s, ";", QUOTES, p);
            int p2 = indexOfNotInQuotes(s, "{", QUOTES, p);
            int p3 = indexOfNotInQuotes(s, "}", QUOTES, p);
            int p4 = indexOfNotInQuotes(s, "else", QUOTES, p);
            int p5 = indexOfNotInQuotes(s, "]", QUOTES, p);
            int p0 = p1;
            int plen = 1;
            if ( 0 <= p5 && p5 < p2 ) { // format new int[]{3,5}; => new int[]{3,5}
                sentence = s.substring(p5+1,p2);
                if ( isEmpty(sentence) ) { p2 = -1; p3 = -1; }
            }
            if ( 0 <= p2 && p2 < p0) p0 = p2;
            if ( 0 <= p3 && p3 < p0) p0 = p3;
            if ( 0 <= p4 && p4 < p0) { p0 = p4; plen = 4; }
            if ( p0 < 0 ) { // take the rest of line
                sentence = s.substring(p);
                if ( !isEmpty(sentence) ) sentences.add(sentence);
                break;
            }
            sentence = s.substring(p,p0);
            if ( !isEmpty(sentence) ) sentences.add(sentence);
            separator = s.substring(p0,p0+plen);
            sentences.add(separator + " ");
            p = p0+plen;

        }
        return sentences;
    }
}
