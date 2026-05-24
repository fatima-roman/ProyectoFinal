package exceptions;

/**
 * Thrown when a {@link model.Teacher} cannot be found in the system by its ID.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class TeacherNotFoundException extends Exception {

    /**
     * Constructs the exception with the missing teacher ID.
     *
     * @param id the ID that was not found
     */
    public TeacherNotFoundException(int id) {
        super("Teacher with ID " + id + " was not found in the system.");
    }

    /**
     * Constructs the exception with a custom message.
     *
     * @param message the detail message
     */
    public TeacherNotFoundException(String message) {
        super(message);
    }
}
