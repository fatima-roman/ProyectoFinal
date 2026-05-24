package service;

import exceptions.DuplicateEnrollmentException;
import exceptions.GradeOutOfRangeException;
import exceptions.StudentNotFoundException;
import exceptions.SubjectNotFoundException;
import model.Enrollment;
import model.Student;
import model.Subject;
import repository.EnrollmentDAO;
import repository.StudentDAO;
import repository.SubjectDAO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business service for enrollment management.
 * Handles enrollment creation, grade updates and deletions,
 * enforcing all business rules before any database access.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class EnrollmentService {

    /** DAO used to interact with the ENROLLMENT table. */
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    /** DAO used to resolve students by ID. */
    private final StudentDAO studentDAO = new StudentDAO();

    /** DAO used to resolve subjects by ID. */
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Returns all enrollments.
     *
     * @return list of all enrollments (may be empty, never {@code null})
     */
    public List<Enrollment> findAll() {
        return enrollmentDAO.findAll();
    }

    /**
     * Finds an enrollment by ID.
     *
     * @param id the enrollment identifier
     * @return the matching {@link Enrollment}
     * @throws IllegalArgumentException if no enrollment exists with that id
     */
    public Enrollment findById(int id) {
        Enrollment e = enrollmentDAO.findById(id);
        if (e == null) throw new IllegalArgumentException("Enrollment with ID " + id + " was not found.");
        return e;
    }

    /**
     * Returns all enrollments for a specific student.
     *
     * @param studentId the student identifier
     * @return list of enrollments for that student
     */
    public List<Enrollment> findByStudentId(int studentId) {
        return enrollmentDAO.findByStudentId(studentId);
    }

    /**
     * Enrolls a student in a subject with two partial grades.
     *
     * @param studentId the student identifier
     * @param subjectId the subject identifier
     * @param g1        first partial grade (0-10)
     * @param g2        second partial grade (0-10)
     * @throws StudentNotFoundException     if the student does not exist
     * @throws SubjectNotFoundException     if the subject does not exist
     * @throws GradeOutOfRangeException     if any grade is outside [0, 10]
     * @throws DuplicateEnrollmentException if the student is already enrolled
     */
    public void enroll(int studentId, int subjectId, double g1, double g2)
            throws StudentNotFoundException, SubjectNotFoundException,
                   GradeOutOfRangeException, DuplicateEnrollmentException {
        Student student = studentDAO.findById(studentId);
        if (student == null) throw new StudentNotFoundException(studentId);
        Subject subject = subjectDAO.findById(subjectId);
        if (subject == null) throw new SubjectNotFoundException(subjectId);
        if (g1 < 0 || g1 > 10) throw new GradeOutOfRangeException(g1);
        if (g2 < 0 || g2 > 10) throw new GradeOutOfRangeException(g2);
        if (enrollmentDAO.existsByStudentAndSubject(studentId, subjectId))
            throw new DuplicateEnrollmentException(student.getName(), subject.getName());
        enrollmentDAO.save(new Enrollment(0, student, subject, g1, g2));
    }

    /**
     * Updates the grades of an existing enrollment.
     *
     * @param enrollmentId the enrollment identifier
     * @param g1           new first partial grade (0-10)
     * @param g2           new second partial grade (0-10)
     * @throws GradeOutOfRangeException if any grade is outside [0, 10]
     */
    public void updateGrades(int enrollmentId, double g1, double g2)
            throws GradeOutOfRangeException {
        if (g1 < 0 || g1 > 10) throw new GradeOutOfRangeException(g1);
        if (g2 < 0 || g2 > 10) throw new GradeOutOfRangeException(g2);
        Enrollment e = findById(enrollmentId);
        e.setGrade1(g1);
        e.setGrade2(g2);
        enrollmentDAO.update(e);
    }

    /**
     * Deletes an enrollment by ID.
     *
     * @param id the enrollment identifier
     */
    public void deleteEnrollment(int id) {
        findById(id);
        enrollmentDAO.deleteById(id);
    }

    /**
     * Returns enrollments sorted by final grade descending.
     *
     * @return sorted list of enrollments
     */
    public List<Enrollment> findAllSortedByGradeDesc() {
        return enrollmentDAO.findAll().stream()
                .sorted(Comparator.comparingDouble(Enrollment::calculateFinalGrade).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Returns only the enrollments where the student passed.
     *
     * @return list of passed enrollments
     */
    public List<Enrollment> findPassed() {
        return enrollmentDAO.findAll().stream()
                .filter(Enrollment::hasPassed)
                .collect(Collectors.toList());
    }

    /**
     * Returns the average final grade across all enrollments.
     *
     * @return average grade, or 0.0 if no enrollments exist
     */
    public double averageGrade() {
        return enrollmentDAO.findAll().stream()
                .mapToDouble(Enrollment::calculateFinalGrade)
                .average()
                .orElse(0.0);
    }
}
