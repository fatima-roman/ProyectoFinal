package service;

import exceptions.DuplicateEnrollmentException;
import exceptions.GradeOutOfRangeException;
import exceptions.StudentNotFoundException;
import model.Enrollment;
import model.Student;
import model.Subject;
import repository.EnrollmentDAO;
import repository.StudentDAO;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    /** @throws StudentNotFoundException if the ID does not exist */
    public Student findById(int id) throws StudentNotFoundException {
        Student s = studentDAO.findById(id);
        if (s == null) throw new StudentNotFoundException(id); 
        return s;
    }

    /** @throws DuplicateEnrollmentException if you are already enrolled */
    public void enrollStudent(int studentId, Subject subject, double g1, double g2)
            throws StudentNotFoundException, DuplicateEnrollmentException, GradeOutOfRangeException {

        Student student = findById(studentId); // can throw StudentNotFoundException

        // Validation notes
        if (g1 < 0 || g1 > 10) throw new GradeOutOfRangeException(g1);
        if (g2 < 0 || g2 > 10) throw new GradeOutOfRangeException(g2);

        // Verification duplicate
        boolean alreadyEnrolled = student.getEnrollments().stream()
                .anyMatch(e -> e.getSubject().equals(subject));
        if (alreadyEnrolled)
            throw new DuplicateEnrollmentException(student.getName(), subject.getName());

        // All OK → save
        Enrollment enrollment = new Enrollment(0, student, subject, g1, g2);
        enrollmentDAO.save(enrollment);
        student.enrollSubject(enrollment);
    }
}
