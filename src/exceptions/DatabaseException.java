package exceptions;

/**
 * Unchecked exception thrown when a database operation fails.
 * Wraps the original {@link java.sql.SQLException} as the cause.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);}
    public DatabaseException(String message) {
        super(message);
    }
}