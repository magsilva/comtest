// ComTest - Comments for testing
package cc.jyu.fi.comtest.csharp;

import java.text.ParseException;

/**
 * Error message for assertion statements.
 * @author tojukarp
 */
public class AssertErrorMessage {
    //#import java.text.ParseException;

    public String methodName;
    public int codeLine;

    /**
     * Creates a new assertion error message.
     * @param method Method name
     * @param line Line number (in file)
     */
    public AssertErrorMessage(String method, int line) {
        methodName = method;
        codeLine = line;
    }

    /**
     * Converts the message into a string.
     * @return The message as a string
     * <pre name="test">
     * AssertErrorMessage msg = new AssertErrorMessage("TestMethod", 12);
     * msg.toString() === "in method TestMethod, line 12";
     * </pre>
     */
    @Override
    public String toString() {
        return String.format("in method %s, line %d", methodName, codeLine);
    }

    /**
     * Parses a string into an error message object.
     * @param s String to parse
     * @return AssertErrorMessage object
     * @throws ParseException when s contains something unexpected
     * <pre name="test">
     * try {
     *   AssertErrorMessage msg = AssertErrorMessage.parse("in method x, line 6");
     *   msg.methodName === "x";
     *   msg.codeLine === 6;
     * } catch (ParseException e) {
     *   fail(String.format("ParseException on offset %d: %s", e.getErrorOffset(), e.getMessage()));
     * }
     *
     * AssertErrorMessage.parse(""); #THROWS ParseException
     * AssertErrorMessage.parse("in TestMethod, line 6"); #THROWS ParseException
     * AssertErrorMessage.parse("in method TestMethod"); #THROWS ParseException
     * AssertErrorMessage.parse("in method TestMethod, line xyz"); #THROWS ParseException
     * AssertErrorMessage.parse("in method TestMethod, line 2, more stuff"); #THROWS ParseException
     * </pre>
     */
    public static AssertErrorMessage parse(String s) throws ParseException {
        if ( s.length() < 19 ) throw new ParseException("Line is too short", 0);
        if ( !s.substring(0, 10).equals("in method ") ) throw new ParseException("Invalid header", 0);

        int comma = s.indexOf(',');
        if (comma <= 10) throw new ParseException(s, 10);
        String method = s.substring(10, comma);

        if (!s.substring(comma + 1, comma + 7).equals(" line ")) throw new ParseException("Comma not found", comma + 1);
        String lineStr = s.substring(comma + 7);

        try {
            int line = Integer.parseInt(lineStr);
            return new AssertErrorMessage(method, line);
        } catch (NumberFormatException nfe) {
            throw new ParseException("Invalid line number", comma + 7);
        }
    }
}
