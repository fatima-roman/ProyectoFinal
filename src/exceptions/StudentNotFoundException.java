package exceptions;

public class StudentNotFoundException extends Exception {
 public StudentNotFoundException(int id) {
     super("Student with ID " + id + " was not found in the system.");
 }
 public StudentNotFoundException(String message) { super(message); }
}