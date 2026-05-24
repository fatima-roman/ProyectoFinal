package exceptions;

/**
 * Thrown when a {@link model.Student} cannot be found in the system by its ID.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class StudentNotFoundException extends Exception {

    /**
     * Constructs the exception with the missing student ID.
     *
     * @param id the ID that was not found
     */
    public StudentNotFoundException(int id) {
        super("Student with ID " + id + " was not found in the system.");
    }

    /**
     * Constructs the exception with a custom message.
     *
     * @param message the detail message
     */
    public StudentNotFoundException(String message) {
        super(message);
    }
}
