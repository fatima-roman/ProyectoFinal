package service;

import exceptions.DuplicateEnrollmentException;
import exceptions.GradeOutOfRangeException;
import exceptions.StudentNotFoundException;
import model.Enrollment;
import model.Student;
import model.Subject;
import repository.EnrollmentDAO;
import repository.StudentDAO;

import java.util.List;

/**
 * Business service for student management.
 * Acts as the middle layer between the UI and the DAO,
 * enforcing business rules before any database access.
 *
 * @author Fatima
 * @version 1.1
 */
public class StudentService {

    /** DAO for CRUD operations on students. */
    private final StudentDAO studentDAO = new StudentDAO();

    /** DAO for CRUD operations on enrollments. */
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    /**
     * Finds a student by its id.
     *
     * @param id the student identifier
     * @return the matching student
     * @throws StudentNotFoundException if no student exists with that id
     */
    public Student findById(int id) throws StudentNotFoundException {
        Student s = studentDAO.findById(id);
        if (s == null) throw new StudentNotFoundException(id);
        return s;
    }

    /**
     * Returns all registered students.
     *
     * @return list of all students
     */
    public List<Student> findAll() {
        return studentDAO.findAll();
    }

    /**
     * Enrolls a student in a subject with two partial grades.
     * Validates that the student exists, that both grades are within [0, 10],
     * and that the student is not already enrolled in that subject.
     *
     * @param studentId the student's id
     * @param subject   the subject to enroll in
     * @param g1        first partial grade (0–10)
     * @param g2        second partial grade (0–10)
     * @throws StudentNotFoundException     if the student does not exist
     * @throws GradeOutOfRangeException     if any grade is outside [0, 10]
     * @throws DuplicateEnrollmentException if the student is already enrolled in that subject
     */
    public void enrollStudent(int studentId, Subject subject, double g1, double g2)
            throws StudentNotFoundException, DuplicateEnrollmentException, GradeOutOfRangeException {

        Student student = findById(studentId);

        if (g1 < 0 || g1 > 10) throw new GradeOutOfRangeException(g1);
        if (g2 < 0 || g2 > 10) throw new GradeOutOfRangeException(g2);

        // FIX: check for duplicates in the DB, not in the in-memory list
        boolean alreadyEnrolled = enrollmentDAO.existsByStudentAndSubject(studentId, subject.getId());
        if (alreadyEnrolled)
            throw new DuplicateEnrollmentException(student.getName(), subject.getName());

        Enrollment enrollment = new Enrollment(0, student, subject, g1, g2);
        enrollmentDAO.save(enrollment);
        student.enrollSubject(enrollment);
    }

    /**
     * Removes a student from the database.
     *
     * @param id the id of the student to delete
     * @throws StudentNotFoundException if no student exists with that id
     */
    public void deleteStudent(int id) throws StudentNotFoundException {
        findById(id); // throws if not found
        studentDAO.deleteById(id);
    }
}