// ComTest - Comments for testing
package cc.jyu.fi.comtest;

/**
 * ComTest general exception
 * @author tojukarp
 */
public class ComTestException extends Exception {
    public boolean fatal;
    public int codeLine;
    
    public ComTestException(String message, boolean fatal) {
        super(message);
        this.fatal = fatal;
    }
}
