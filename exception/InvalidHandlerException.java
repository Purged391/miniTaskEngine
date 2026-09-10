package exception;

/** Indicates that a handler declaration does not satisfy engine rules. */
public class InvalidHandlerException extends TaskEngineException {

    /** Creates an exception with its underlying cause. */
    public InvalidHandlerException(String message, Throwable e) {
        super(message, e);
    }

    /** Creates an exception with a message. */
    public InvalidHandlerException(String message) {
        super(message);
    }
}
