package comtest.legacy;

/** 
 * This class is just for making test for comtest
 * @author vesal
 *
 */
public class Mock {
    // #STATICIMPORT
    // #import comtest.Strings;
    // #import comtest.ComTestIni;
    
    /**
     * Method just for cheking what the test outputs
     *
     * @example
     * <pre name="test">
     *   Strings contents = new Strings();
     *   comtest.java.ComTestScanner scanner = new comtest.java.ComTestScanner(new ComTestIni(), contents);
     *   try { scanner.findNames("test");  } catch (java.io.IOException e) { } 
     *   Strings out = scanner.getOutlines();
     *   comtest.java.ComTestScanner.CommentCodeHandler codeHandler = scanner.createCommentCodeHandler();
     *   Strings initCmnds = codeHandler.getInitCmnds();
     *
     *   initCmnds.clear();
     *   codeHandler.handleSentences("get(request,new String[]{\"str\",\"id\"},13,true) === 123;");
     *   codeHandler.outNewHeader();
     *   out.get(1) === "kissa";
     *   initCmnds.get(1) === "assertEquals(\"From: test line: 1\", 5, a)";
     *   
     *   
     * </pre>
     */
    public static void first() {
      return;  
    }

    /**
     * @example
     * <pre name="test">
     * int a = 5; int b = 6;
     * if ( a > 4 ) { a === 5; } else  b === 6;    
     * </pre>
     *
     */
    public static void first1() {
        return;  
    }
    
}
