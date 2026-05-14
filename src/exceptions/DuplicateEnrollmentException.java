package exceptions;

public class DuplicateEnrollmentException extends Exception {
    public DuplicateEnrollmentException(String studentName, String subjectName) {
        super("Student \"" + studentName + "\" is already enrolled in subject \"" + subjectName + "\".");
    }
}
