package exception;

/** Indicates that an entity with the same identifier already exists. */
public class DuplicateEntityException extends TaskEngineException {

    /** Creates an exception with its underlying cause. */
    public DuplicateEntityException(String message, Throwable e) {
        super(message, e);
    }
    /** Creates an exception with a message. */
    public DuplicateEntityException(String message) {
        super(message);
    }

}
