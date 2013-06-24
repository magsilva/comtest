package cc.jyu.fi.comtest.utils;

import static org.junit.Assert.*;

/**
 * A collection for things to help testing
 * @author vesal
 *
 */
public class TestUtilities {
    /*     
     #import static comtest.utils.TestUtilities.*;
    */  

    
    /**
     * A helper function to make regExp test by ComTest
     * @param message what message to show
     * @param value what to check
     * @param mask what regExp mask to use 
     * @param dummy just a dummy parameter to overload double assertEquals
     *
     * @example
     * <pre name="test">
     * "cat" ~~~ "c.*t";
     * "cat" ~~~ "c.*a";
     * "cat" =~ "c.*a";
     * </pre>
     */
    public static void assertEquals(String message, String value, String mask, double dummy) {
        if ( value.matches(mask) ) return;
        fail(message + " Does not match: " + value + " to " + mask);
    }
    
}
