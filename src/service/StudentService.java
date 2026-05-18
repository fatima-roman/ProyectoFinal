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
 *
 * Acts as the middle layer between the UI and the DAO,
 * enforcing business rules before any database access.
 *
 * @author Fatima Roman
 * @version 2.0
 */
public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    /**
     * Finds a student by its primary key.
     *
     * @param id the student identifier
     * @return the matching {@link Student}
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
     * @return list of all students (may be empty, never null)
     */
    public List<Student> findAll() {
        return studentDAO.findAll();
    }

    /**
     * Saves a new student to the database.
     *
     * Validates that no student with the same ID already exists
     * before persisting.
     *
     * @param student the student to create; must not be {@code null}
     * @throws IllegalArgumentException if a student with the same ID already exists
     */
    public void save(Student student) {
        if (studentDAO.findById(student.getId()) != null) {
            throw new IllegalArgumentException(
                "A student with ID " + student.getId() + " already exists.");
        }
        studentDAO.save(student);
    }

    /**
     * Updates an existing student record in the database.
     *
     * Validates that the student actually exists before updating.
     *
     * @param student the student with updated values; must not be {@code null}
     * @throws StudentNotFoundException if no student exists with that id
     */
    public void update(Student student) throws StudentNotFoundException {
        findById(student.getId()); // throws StudentNotFoundException if not found
        studentDAO.update(student);
    }

    /**
     * Removes a student and all their enrollments from the database.
     *
     * @param id the id of the student to delete
     * @throws StudentNotFoundException if no student exists with that id
     */
    public void deleteStudent(int id) throws StudentNotFoundException {
        findById(id); // throws if not found
        studentDAO.deleteById(id);
    }

    /**
     * Enrolls a student in a subject with two partial grades.
     *
     * Business rules enforced:
     * <ul>
     *   <li>Student must exist.</li>
     *   <li>Both grades must be within [0, 10].</li>
     *   <li>Student must not already be enrolled in the subject.</li>
     * </ul>
     *
     * @param studentId the student's id
     * @param subject   the subject to enroll in
     * @param g1        first partial grade (0–10)
     * @param g2        second partial grade (0–10)
     * @throws StudentNotFoundException      if the student does not exist
     * @throws GradeOutOfRangeException      if any grade is outside [0, 10]
     * @throws DuplicateEnrollmentException  if the student is already enrolled
     */
    public void enrollStudent(int studentId, Subject subject, double g1, double g2)
            throws StudentNotFoundException, DuplicateEnrollmentException, GradeOutOfRangeException {

        Student student = findById(studentId);

        if (g1 < 0 || g1 > 10) throw new GradeOutOfRangeException(g1);
        if (g2 < 0 || g2 > 10) throw new GradeOutOfRangeException(g2);

        boolean alreadyEnrolled = enrollmentDAO.existsByStudentAndSubject(studentId, subject.getId());
        if (alreadyEnrolled)
            throw new DuplicateEnrollmentException(student.getName(), subject.getName());

        Enrollment enrollment = new Enrollment(0, student, subject, g1, g2);
        enrollmentDAO.save(enrollment);
        student.enrollSubject(enrollment);
    }
}