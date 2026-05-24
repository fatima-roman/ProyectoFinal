package exceptions;

/**
 * Thrown when a grade value is outside the valid range [0.0, 10.0].
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class GradeOutOfRangeException extends Exception {

    /**
     * Constructs the exception with the offending grade value.
     *
     * @param grade the grade that caused the exception
     */
    public GradeOutOfRangeException(double grade) {
        super("Grade value " + grade + " is out of the valid range [0.0 - 10.0].");
    }
}
