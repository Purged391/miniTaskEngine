package exception;

/** Indicates that no handler is registered for a task type. */
public class HandlerNotFoundException extends TaskEngineException {

    /** Creates an exception with its underlying cause. */
    public HandlerNotFoundException(String message, Throwable e) {
        super(message, e);
    }
    /** Creates an exception with a message. */
    public HandlerNotFoundException(String message) {
        super(message);
    }
}
