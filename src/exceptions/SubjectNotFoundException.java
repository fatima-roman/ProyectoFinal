package exceptions;

/**
 * Thrown when a {@link model.Subject} cannot be found in the system by its ID.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class SubjectNotFoundException extends Exception {

    /**
     * Constructs the exception with the missing subject ID.
     *
     * @param id the ID that was not found
     */
    public SubjectNotFoundException(int id) {
        super("Subject with ID " + id + " was not found in the system.");
    }


}
