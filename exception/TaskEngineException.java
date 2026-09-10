package exception;

/** Base unchecked exception for task engine failures. */
public class TaskEngineException extends RuntimeException {
    /** Creates an exception with a message and its underlying cause. */
    public TaskEngineException(String message, Throwable e) {
        super(message, e);
    }

    /** Creates an exception with a message. */
    public TaskEngineException(String message) {
        super(message);
    }
}
