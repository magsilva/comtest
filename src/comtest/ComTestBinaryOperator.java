// ComTest - Comments for testing
package comtest;

import comtest.csharp.AssertErrorMessage;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static comtest.utils.StringUtilities.*;

/**
 * ComTest binary operator
 * @author tojukarp
 */
public class ComTestBinaryOperator extends ComTestOperator {
    List<String> aliases = new ArrayList<String>();
    String formatStr;
    boolean reverseOrder;

    /**
     * Creates a new ComTest binary operator.
     * @param format
     *   Format string of the code line to generate, for example
     *     Assert.AreEqual(%s, %s, %s);
     *   The format string must have 3 (or 4 if third parameter is used)
     *   "%s" (string type) template markers in it.
     * @param reverse
     *   Whether to reverse the actual (what is) and expected (what should be)
     *   operands. The default is first actual, then expected.
     * @param opers Operator aliases
     */
    public ComTestBinaryOperator(String format, boolean reverse, String... opers) {
        aliases.addAll(Arrays.asList(opers));
        formatStr = format;
        reverseOrder = reverse;
    }

    /**
     * Applies the test operator.
     * @param s String possibly containing an operator
     * @param errorMessage Error message to show if assertion fails
     * @param additional Additional parameters
     * @return String containing the assertion code
     * @example
     * <pre name="test">
     * ComTestBinaryOperator eq = new ComTestBinaryOperator("Assert.AreEqual(%s, %s, \"%s\");", false, "eq");
     * ComTestBinaryOperator eq2 = new ComTestBinaryOperator("Assert.AreEqual(%s, %s, \"%s\");", true, "eq");
     * ComTestBinaryOperator almost = new ComTestBinaryOperator("Assert.AreEqual(%s, %s, %s, \"%s\");", false, "alm");
     * AssertErrorMessage msg = new AssertErrorMessage("testMethod", 10);
     * String msgStr = msg.toString();
     * eq.apply("1eq2", msg) === String.format("Assert.AreEqual(2, 1, %s);", msgStr);
     * eq.apply("1eq2", "error") === String.format("Assert.AreEqual(2, 1, %s);", msgStr);
     * eq2.apply("1eq2", "error") === String.format("Assert.AreEqual(1, 2, %s);", msgStr);
     * almost.apply("1alm2", "error", "0.5") === "Assert.AreEqual(2, 1, 0.5, %s);", msgStr);
     * </pre>
     */
    public String apply(String s, AssertErrorMessage errorMessage, String... additional) {
        for ( int i = 0; i < aliases.size(); i++ ) {
            String oper = aliases.get(i);
            int eqIndex = indexOfNotInQuotes(s, oper);
            if ( eqIndex < 0 ) continue;

            int eos = indexOfNotInQuotes(s, ";");

            if ( eos < 0 )
                // No semicolon, so end of line ends the sentence
                eos = s.length();

            String actual = reverseOrder ?
                s.substring(eqIndex + oper.length(), eos) :
                s.substring(0, eqIndex);

            String expected = reverseOrder ?
                s.substring(0, eqIndex) :
                s.substring(eqIndex + oper.length(), eos);

            if ( additional.length == 0 )
                return String.format(formatStr, expected, actual, errorMessage.toString());
            else {
                String line = formatStr.replaceFirst("%s", Matcher.quoteReplacement(expected));
                line = line.replaceFirst("%s", Matcher.quoteReplacement(actual));
                
                for ( int j = 0; j < additional.length; j++ ) {
                    line = line.replaceFirst("%s", additional[j]);
                }

                return line.replaceFirst("%s", errorMessage.toString());
            }
        }

        return s;
    }

}
