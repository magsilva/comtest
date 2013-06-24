// ComTest - Comments for testing
package cc.jyu.fi.comtest;

/**
 * Result for an operation
 * @author tojukarp
 */
public abstract class ComTestResult {
    protected String msg;
    protected Object obj;
    
    public Object getObject() { return obj; };
    public String getMessage() { return msg; };

    public ComTestResult(String message, Object object) {
        msg = message;
        obj = object;
    }

    public static class Success extends ComTestResult {
        public Success(String message) {
            super(message, null);
        }

        public Success(String message, Object object) {
            super(message, object);
        }
    }

    public static class Fail extends ComTestResult {
        public Fail(String message) {
            super(message, null);
        }

        public Fail(String message, Object object) {
            super(message, object);
        }
    }

    public static class InternalError extends Fail {
        public InternalError(String message) {
            super(message, null);
        }

        public InternalError(String message, Object object) {
            super(message, object);
        }
    }

    public static class SkippedAsBad extends ComTestResult {
        public SkippedAsBad(String message) {
            super(message, null);
        }

        public SkippedAsBad(String message, Object object) {
            super(message, object);
        }
    }

    public static class SkippedAsGood extends ComTestResult {
        public SkippedAsGood(String message) {
            super(message, null);
        }

        public SkippedAsGood(String message, Object object) {
            super(message, object);
        }
    }
}
