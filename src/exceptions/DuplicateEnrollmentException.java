package exceptions;

/**
 * Thrown when a student is enrolled in a subject they are already registered for.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class DuplicateEnrollmentException extends Exception {

    /**
     * Constructs the exception identifying the student and subject involved.
     *
     * @param studentName the name of the student
     * @param subjectName the name of the subject
     */
    public DuplicateEnrollmentException(String studentName, String subjectName) {
        super("Student \"" + studentName + "\" is already enrolled in subject \"" + subjectName + "\".");
    }
}
