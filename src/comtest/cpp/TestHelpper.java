// ComTest - Comments for testing
package comtest.cpp;

import comtest.ComTestIni;
import comtest.utils.Strings;
import static comtest.ComTestSyntax.*;

/**
 * A class to help testing ComTest-functions.
 * @author vesal
 *
 */
public class TestHelpper {
    /** contents of the files       */
    public Strings contents;

    /** Global test scanner         */
    public comtest.cpp.ComTestScanner scanner;

    /** Global test codeHandler     */
    public comtest.cpp.ComTestScanner.CommentCodeHandler codeHandler;

    /** Global list for test initCmnds */
    public Strings initCmnds;

    /** Global list for test templates */
    public Strings template;


    /** Global list for test outLines  */
    public Strings outLines;

    /** Import lines */
    public Strings imports;

    /**
     * Initializer for TestHelper
     */
    public TestHelpper() {
      contents = new Strings();
      scanner = new comtest.cpp.ComTestScanner(new ComTestIni(), contents);
      try { scanner.findNames(TEST);  } catch (java.io.IOException e) {
          System.err.println(e.getMessage());
      }
      codeHandler = scanner.createCommentCodeHandler();
      initCmnds = codeHandler.getInitCmnds();
      template  = codeHandler.getTemplate();
      outLines = scanner.getOutlines();
      imports = scanner.getImports();
    }

    /**
     * Initializer for TestHelper
     * @param methodName Method name to use
     */
    public TestHelpper(String methodName) {
        this();
        codeHandler.setMethodName(methodName);
    }


    /** Global list of outlines */
    public String[] out = null;


    /**
     * Gte the outlines as array
     * @return array of printed lines
     */
    public String[] getOut() {
            StringBuilder sb = new StringBuilder();
        for (String s:outLines) sb.append(s);
        out = sb.toString().split("\n");
        return out;
    }


    /**
     * Scan the contents
     * @param i what line to start the scan
     */
    public void scan(int i) {
            codeHandler.readCode(i);
            getOut();
    }

    /**
     * Scan the contents
     * @param s line to scan
     */
    public void scan(String s) {
            contents.clear();
            contents.add(s);
            outLines.clear();
            codeHandler.getInitCmnds().clear();
            codeHandler.readCode(0);
            getOut();
    }


    /**
     * Scans a line and returns ComTest output for that line
     * @param s list to scan
     * @return arrays of ComTest strings
     * @example
     * <pre name="test">
     *    String out[] = TestHelpper.scanLine("a === 5");
     *    out[1].trim() =R= "assertEquals\\(\".*\", 5, a\\);"
     * </pre>
     */
    public static String[] scanLine(String s) {
       TestHelpper t = new TestHelpper("scan");
       t.scan(s);
       return t.getOut();
    }


    /**
     * Scans a line and returns ComTest trimmed output for that line
     * @param s list to scan
     * @return arrays of ComTest strings trimmed
     * @example
     * <pre name="test">
     *    String out[] = TestHelpper.scanLineTrim("a === 5");
     *    out[1].trim() =R= "assertEquals\\(\".*\", 5, a\\);"
     * </pre>
     */
    public static String[] scanLineTrim(String s) {
       TestHelpper t = new TestHelpper("scan");
       t.scan(s);
       String lines[] = t.getOut();
       for (int i=0;i<lines.length;i++) lines[i] = lines[i].trim();
       return lines;
    }


    /**
     * Scans line to one line
     * @param s line to scan
     * @return scanned line compiled by comtest
     * @example
     * <pre name="test">
     *    String out = TestHelpper.scanToLine("(Exception ex) { ex.getMessage() =R= \".*ex.*\"; }");
     *    out =R= "\\(Exception ex\\) \\{.*ex.getMessage.* if.*matches.*fail.*\\}";
     * </pre>
     */
    public static String scanToLine(String s) {
            String lines[] = scanLineTrim(s);
            StringBuilder line = new StringBuilder();
            for (int i=1; i<lines.length;i++) line.append(" "+lines[i]);
        return line.toString().trim();
     }
}
