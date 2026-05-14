package exceptions;

public class GradeOutOfRangeException extends Exception {
    public GradeOutOfRangeException(double grade) {
        super("Grade value " + grade + " is out of the valid range [0.0 – 10.0].");
    }
}
