package service;

import exceptions.DuplicateEnrollmentException;
import exceptions.GradeOutOfRangeException;
import exceptions.StudentNotFoundException;
import model.Enrollment;
import model.Student;
import model.Subject;
import repository.EnrollmentDAO;
import repository.StudentDAO;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business service for student management.
 * Acts as the middle layer between the UI and {@link StudentDAO},
 * enforcing business rules before any database access.
 *
 * @author Fatima Roman
 * @version 2.2
 */
public class StudentService {

    /** DAO used to interact with the STUDENT table. */
    private final StudentDAO studentDAO = new StudentDAO();

    /** DAO used to interact with the ENROLLMENT table. */
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
     * @return list of all students (may be empty, never {@code null})
     */
    public List<Student> findAll() {
        return studentDAO.findAll();
    }

    /**
     * Saves a new student to the database.
     *
     * @param student the student to create; must not be {@code null}
     * @throws IllegalArgumentException if a student with the same ID already exists
     */
    public void save(Student student) {
        if (studentDAO.findById(student.getId()) != null)
            throw new IllegalArgumentException("A student with ID " + student.getId() + " already exists.");
        studentDAO.save(student);
    }

    /**
     * Updates an existing student record.
     *
     * @param student the student with updated values; must not be {@code null}
     * @throws StudentNotFoundException if no student exists with that id
     */
    public void update(Student student) throws StudentNotFoundException {
        findById(student.getId());
        studentDAO.update(student);
    }

    /**
     * Removes a student and all their enrollments from the database.
     *
     * @param id the id of the student to delete
     * @throws StudentNotFoundException if no student exists with that id
     */
    public void deleteStudent(int id) throws StudentNotFoundException {
        findById(id);
        studentDAO.deleteById(id);
    }

    /**
     * Enrolls a student in a subject with two partial grades.
     *
     * @param studentId the student's id
     * @param subject   the subject to enroll in
     * @param g1        first partial grade (0-10)
     * @param g2        second partial grade (0-10)
     * @throws StudentNotFoundException     if the student does not exist
     * @throws GradeOutOfRangeException     if any grade is outside [0, 10]
     * @throws DuplicateEnrollmentException if the student is already enrolled
     */
    public void enrollStudent(int studentId, Subject subject, double g1, double g2)
            throws StudentNotFoundException, DuplicateEnrollmentException, GradeOutOfRangeException {
        Student student = findById(studentId);
        if (g1 < 0 || g1 > 10) throw new GradeOutOfRangeException(g1);
        if (g2 < 0 || g2 > 10) throw new GradeOutOfRangeException(g2);
        if (enrollmentDAO.existsByStudentAndSubject(studentId, subject.getId()))
            throw new DuplicateEnrollmentException(student.getName(), subject.getName());
        Enrollment enrollment = new Enrollment(0, student, subject, g1, g2);
        enrollmentDAO.save(enrollment);
        student.enrollSubject(enrollment);
    }

    /**
     * Returns all students sorted by surname.
     *
     * @return sorted list of students
     */
    public List<Student> findAllSortedBySurname() {
        return studentDAO.findAll().stream()
                .sorted(Comparator.comparing(Student::getSurname))
                .collect(Collectors.toList());
    }

    /**
     * Filters students by their year.
     *
     * @param year the year to filter by (1 or 2)
     * @return list of students in that year
     */
    public List<Student> findByYear(int year) {
        return studentDAO.findAll().stream()
                .filter(s -> s.getStudentYear() == year)
                .collect(Collectors.toList());
    }

    /**
     * Returns the names of all students.
     *
     * @return list of "Name Surname" strings
     */
    public List<String> getAllStudentNames() {
        return studentDAO.findAll().stream()
                .map(s -> s.getName() + " " + s.getSurname())
                .collect(Collectors.toList());
    }

    /**
     * Groups students by their year.
     *
     * @return map from year to list of students
     */
    public Map<Integer, List<Student>> groupByYear() {
        return studentDAO.findAll().stream()
                .collect(Collectors.groupingBy(Student::getStudentYear));
    }

    /**
     * Returns the number of students who have passed (average grade >= 5).
     *
     * @return count of students who have passed
     */
    public long countPassed() {
        return studentDAO.findAll().stream()
                .filter(Student::hasPassed)
                .count();
    }

    /**
     * Returns the average final grade across all students.
     *
     * @return average grade, or 0.0 if no students exist
     */
    public double averageGrade() {
        return studentDAO.findAll().stream()
                .mapToDouble(Student::calculateFinalGrade)
                .average()
                .orElse(0.0);
    }

    /**
     * Builds an index of students keyed by their ID for fast lookup.
     * Uses {@link HashMap} to provide O(1) access by key, more efficient
     * than iterating a list when frequent lookups by ID are needed.
     *
     * @return map of student ID to {@link Student} object
     */
    public Map<Integer, Student> buildIndexById() {
        Map<Integer, Student> index = new HashMap<>();
        for (Student s : studentDAO.findAll()) {
            index.put(s.getId(), s);
        }
        return index;
    }
}