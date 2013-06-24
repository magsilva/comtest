// ComTest - Comments for testing
package comtest.csharp;

import static comtest.utils.StringUtilities.*;
import comtest.utils.Strings;
import java.text.ParseException;
import java.util.Collection;

/**
 * Object representation of a C# assertion statement.
 * @author tojukarp
 */
public class AssertLine {
    //#import comtest.csharp.AssertLine;
    //#import java.text.ParseException;
    
    public String assertMethod;
    public Strings params;

    public AssertLine(String method, Collection<? extends String> parameters) {
        assertMethod = method;
        params = new Strings(parameters);
    }

    public AssertLine(String method, String... parameters) {
        assertMethod = method;
        params = new Strings(parameters);
    }

    /**
     * Parses an object from a string.
     * @param s Source string
     * @return AssertLine object
     * @throws ParseException when s contains something unexpected
     * <pre name="test">
     * try {
     *   AssertLine l = AssertLine.parse("   Assert.AreEqual( exp, act );");
     *   l.assertMethod === "AreEqual";
     *   l.params.get(0) === "exp";
     *   l.params.get(1) === "act";
     * } catch (ParseException e) {
     *   fail(String.format("ParseException on offset %d: %s", e.getErrorOffset(), e.getMessage()));
     * }
     *
     * AssertLine.parse("Assert.AreEqual(exp, act)"); #THROWS ParseException
     * AssertLine.parse("Assert.AreEqual(exp)"); #THROWS ParseException
     * AssertLine.parse("AreEqual(exp, act)"); #THROWS ParseException
     * </pre>
     */
    public static AssertLine parse(String s) throws ParseException {
        String str = s.trim();
        if ( !str.startsWith("Assert.") || !str.endsWith(";") )
            throw new ParseException( "Is not an assert statement", 0 );

        int openpi = str.indexOf('(');
        int closepi = str.lastIndexOf(')');
        if ( openpi < 7 || openpi > closepi )
            throw new ParseException( "Unmatched parentheses", openpi );

        String method = str.substring(7, openpi);
        
        String paramStr = str.substring(openpi + 1, closepi);
        Strings params = splitBy(paramStr, ",", 1, true, " ");
        if ( params.size() < 2 ) throw new ParseException( "Too few arguments", openpi );

        return new AssertLine(method, params);
    }

    /**
     * Gets the expected result of the statement.
     * @return Expected result
     */
    public String getExpected() {
        return params.get(0);
    }

    /**
     * Sets the expected result of the statement.
     * @param Expected result
     */
    public void setExpected(String value) {
        params.set(0, value);
    }

    /**
     * Gets the actual result of the statement.
     * @return Actual result
     */
    public String getActual() {
        return params.get(1);
    }

    /**
     * Sets the actual result of the statement.
     * @param Actual result
     */
    public void setActual(String value) {
        params.set(1, value);
    }

    /**
     * Gets the error message displayed if the assertion fails.
     * The error message is contained in an object.
     * @return Error message object
     * <pre name="test">
     * AssertLine line = new AssertLine("AreEqual", "a", "b", "in method tstM, line 33");
     * AssertErrorMessage msg = line.getMessage();
     * msg.methodName === "tstM";
     * msg.codeLine === 33;
     *
     * AssertLine line2 = new AssertLine("AreEqual", "a", "b");
     * line2.getMessage() === null;
     *
     * try {
     *   AssertLine l = AssertLine.parse("Assert.Eq( a, b, \"in method x, line 4\" );");
     *   l.assertMethod === "Eq";
     *   l.params.get(0) === "a";
     *   l.params.get(1) === "b";
     *   msg = l.getMessage();
     *   msg.methodName === "x";
     *   msg.codeLine === 4;
     * } catch (ParseException e) {
     *   fail(String.format("ParseException on offset %d: %s", e.getErrorOffset(), e.getMessage()));
     * }
     * </pre>
     */
    public AssertErrorMessage getMessage() {
        try {
            String lastParam = params.get(params.size() - 1);
            return AssertErrorMessage.parse( unquote(lastParam) );
        } catch ( ParseException e ) {
            return null;
        }
    }

    /**
     * Sets the error message displayed if the assertion fails.
     * The error message is contained in an object.
     * @param Error message object
     * <pre name="test">
     * AssertLine line1 = new AssertLine("AreEqual", "a", "b", "in method orig, line 33");
     * AssertLine line2 = new AssertLine("AreEqual", "c", "d");
     * AssertErrorMessage msg = new AssertErrorMessage("newMethod", 60);
     * line1.setMessage(msg);
     * line1.params.size() === 3;
     * AssertErrorMessage msg1 = line1.getMessage();
     * msg1.methodName === "newMethod";
     * msg1.codeLine === 60;
     * 
     * line2.setMessage(msg);
     * line2.params.size() === 3;
     * AssertErrorMessage msg2 = line2.getMessage();
     * msg2.methodName === "newMethod";
     * msg2.codeLine === 60;
     * </pre>
     */
    public void setMessage(AssertErrorMessage msg) {
        String quotedMsg = quote(msg.toString());

        if ( getMessage() == null )
            params.add(quotedMsg);
        else
            params.set(params.size() - 1, quotedMsg);
    }

    @Override
    public String toString() {
        return String.format("Assert.%s(%s);", assertMethod, params.toString(", "));
    }
}
