package data;

/**
 * Wraps checked I/O/serialization failures from SerializedObjectStore
 * as unchecked, matching how the rest of the model layer signals
 * failure (IllegalArgumentException/IllegalStateException) rather
 * than forcing callers up the stack to declare throws clauses.
 */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
