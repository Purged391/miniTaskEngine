package exception;

/** Indicates that a task handler could not process a task. */
public class TaskExecutionException extends TaskEngineException {

    /** Creates an execution exception with its underlying cause. */
    public TaskExecutionException(String message, Throwable e) {
        super(message, e);
    }
    /** Creates an execution exception with a message. */
    public TaskExecutionException(String message) {
        super(message);
    }
}
