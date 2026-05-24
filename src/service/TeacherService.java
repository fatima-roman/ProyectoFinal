package service;

import exceptions.TeacherNotFoundException;
import exceptions.SubjectNotFoundException;
import model.Subject;
import model.Teacher;
import repository.SubjectDAO;
import repository.TeacherDAO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business service for teacher management.
 * Acts as the middle layer between the UI and {@link TeacherDAO},
 * enforcing business rules before any database access.
 *
 * @author Fatima Roman
 * @version 1.2
 */
public class TeacherService {

    /** DAO used to interact with the TEACHER table. */
    private final TeacherDAO teacherDAO = new TeacherDAO();

    /** DAO used to interact with the SUBJECT table when assigning subjects. */
    private final SubjectDAO subjectDAO = new SubjectDAO();

    // ── CRUD ──────────────────────────────────────────────────────────────

    /**
     * Returns all registered teachers.
     *
     * @return list of all teachers (may be empty, never {@code null})
     */
    public List<Teacher> findAll() {
        return teacherDAO.findAll();
    }

    /**
     * Finds a teacher by its primary key.
     *
     * @param id the teacher identifier
     * @return the matching {@link Teacher}
     * @throws TeacherNotFoundException if no teacher exists with that ID
     */
    public Teacher findById(int id) throws TeacherNotFoundException {
        Teacher t = teacherDAO.findById(id);
        if (t == null) throw new TeacherNotFoundException(id);
        return t;
    }

    /**
     * Persists a new teacher after validation.
     *
     * @param teacher the teacher to create; must not be {@code null}
     * @throws IllegalArgumentException if any required field is blank
     */
    public void save(Teacher teacher) {
        validateTeacher(teacher);
        teacherDAO.save(teacher);
    }

    /**
     * Updates an existing teacher record after validation.
     *
     * @param teacher the teacher with updated values; must not be {@code null}
     * @throws TeacherNotFoundException if no teacher exists with that ID
     * @throws IllegalArgumentException if any required field is blank
     */
    public void update(Teacher teacher) throws TeacherNotFoundException {
        findById(teacher.getId());
        validateTeacher(teacher);
        teacherDAO.update(teacher);
    }

    /**
     * Deletes a teacher by ID.
     *
     * @param id the teacher ID to delete
     * @throws TeacherNotFoundException if no teacher exists with that ID
     */
    public void deleteTeacher(int id) throws TeacherNotFoundException {
        findById(id);
        teacherDAO.deleteById(id);
    }

    // ── Subject assignment ────────────────────────────────────────────────

    /**
     * Assigns a subject to a teacher.
     *
     * @param teacherId the teacher ID
     * @param subjectId the subject ID
     * @throws TeacherNotFoundException if the teacher does not exist
     * @throws SubjectNotFoundException if the subject does not exist
     */
    public void assignSubject(int teacherId, int subjectId)
            throws TeacherNotFoundException, SubjectNotFoundException {
        Teacher t = findById(teacherId);
        Subject s = subjectDAO.findById(subjectId);
        if (s == null) throw new SubjectNotFoundException(subjectId);
        t.assignSubject(s);
        s.assignTeacher(t);
        teacherDAO.update(t);
        subjectDAO.update(s);
    }

    /**
     * Removes the subject assignment from a teacher.
     *
     * @param teacherId the teacher ID
     * @param subjectId the subject ID
     * @throws TeacherNotFoundException if the teacher does not exist
     * @throws SubjectNotFoundException if the subject does not exist
     */
    public void unassignSubject(int teacherId, int subjectId)
            throws TeacherNotFoundException, SubjectNotFoundException {
        Teacher t = findById(teacherId);
        Subject s = subjectDAO.findById(subjectId);
        if (s == null) throw new SubjectNotFoundException(subjectId);
        t.unassignSubject(s);
        s.setTeacher(null);
        teacherDAO.update(t);
        subjectDAO.update(s);
    }

    // ── Stream operations ─────────────────────────────────────────────────

    /**
     * Returns all teachers sorted by surname.
     *
     * @return sorted list of teachers
     */
    public List<Teacher> findAllSortedBySurname() {
        return teacherDAO.findAll().stream()
                .sorted(Comparator.comparing(Teacher::getSurname))
                .collect(Collectors.toList());
    }

    /**
     * Filters teachers by specialty keyword (case-insensitive).
     *
     * @param keyword the keyword to search for
     * @return list of teachers whose specialty contains the keyword
     */
    public List<Teacher> findBySpecialty(String keyword) {
        return teacherDAO.findAll().stream()
                .filter(t -> t.getSpecialty().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the teacher with the most subjects assigned.
     *
     * @return optional containing the busiest teacher
     */
    public Optional<Teacher> findBusiestTeacher() {
        return teacherDAO.findAll().stream()
                .max(Comparator.comparingInt(t -> t.getSubjects().size()));
    }

    /**
     * Returns the full names of all teachers.
     *
     * @return list of "Name Surname" strings
     */
    public List<String> getAllTeacherFullNames() {
        return teacherDAO.findAll().stream()
                .map(t -> t.getName() + " " + t.getSurname())
                .collect(Collectors.toList());
    }

    /**
     * Groups teachers by their specialty.
     *
     * @return map from specialty string to list of teachers
     */
    public Map<String, List<Teacher>> groupBySpecialty() {
        return teacherDAO.findAll().stream()
                .collect(Collectors.groupingBy(Teacher::getSpecialty));
    }

    // ── Validation ────────────────────────────────────────────────────────

    /**
     * Validates that all required teacher fields are non-blank.
     *
     * @param t the teacher to validate
     * @throws IllegalArgumentException if any required field is blank
     */
    private void validateTeacher(Teacher t) {
        if (t.getName() == null || t.getName().isBlank())
            throw new IllegalArgumentException("Teacher name cannot be blank.");
        if (t.getSurname() == null || t.getSurname().isBlank())
            throw new IllegalArgumentException("Teacher surname cannot be blank.");
        if (t.getSpecialty() == null || t.getSpecialty().isBlank())
            throw new IllegalArgumentException("Teacher specialty cannot be blank.");
        if (t.getEmail() == null || t.getEmail().isBlank())
            throw new IllegalArgumentException("Teacher email cannot be blank.");
    }
}
