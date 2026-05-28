package exceptions;

/**
 * Unchecked exception thrown when a database operation fails.
 * Wraps the original {@link java.sql.SQLException} as the cause.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class DatabaseException extends RuntimeException {

    /**
     * Constructs the exception with a message and underlying cause.
     *
     * @param message human-readable description of the failure
     * @param cause   the original {@link java.sql.SQLException}
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
