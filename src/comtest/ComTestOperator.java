// ComTest - Comments for testing
package comtest;

import comtest.csharp.AssertErrorMessage;
import comtest.utils.Strings;
import static comtest.utils.StringUtilities.*;

/**
 * Common base class for all ComTest operators
 * @author tojukarp
 */
public abstract class ComTestOperator {
    /**
     * Searches for a ComTest operator in a string.
     * If the operator was found, the return value is the comparison code
     * generated for the input line, otherwise it's the input line itself.
     * @param s Input line (line to apply the operator to)
     * @param errorMessage Error message to display if comparison fails
     * @param additional Additional parameters
     * @return Line s with operator applied to it
     */
    public abstract String apply(String s, AssertErrorMessage errorMessage, String... additional);

    /**
     * Applies a ComTest operator in a string until it cannot be applied
     * anymore.
     * @param s Input line (line to apply the operator to)
     * @param errorMessage Error message to display if comparison fails
     * @param additional Additional parameters
     * @return Lines s with operator applied to it
     */
    public Strings applyAll(String s, AssertErrorMessage errorMessage, String... additional) {
        Strings statements = splitBy(s, ";");

        // Append ; to all except the last line
        for ( int i = 0; i < statements.size() - 1; i++ ) {
            statements.set(i, statements.get(i) + ";");
        }

        // Remove empty last line
        if ( statements.get(statements.size() - 1).length() == 0 )
            statements.remove(statements.size() - 1);
        
        for ( int i = 0; i < statements.size(); i++ ) {
            String newStatement = apply(statements.get(i), errorMessage, additional);

            if ( i < statements.size() - 1 )
                statements.set(i, newStatement + ";");
            else
                statements.set(i, newStatement);
        }

        return statements;
    }

    /**
     * Applies a ComTest operator in multiple strings until it cannot be applied
     * anymore.
     * @param s Input lines
     * @param errorMessage Error message to display if comparison fails
     * @param additional Additional parameters
     * @return Lines s with operator applied to it
     */
    public Strings applyAll(Strings s, AssertErrorMessage errorMessage, String... additional) {
        Strings output = new Strings();

        for ( int i = 0; i < s.size(); i++ )
            output.add(applyAll(s.get(i), errorMessage, additional));

        return output;
    }
}
