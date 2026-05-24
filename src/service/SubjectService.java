package service;

import exceptions.SubjectNotFoundException;
import model.Subject;
import repository.SubjectDAO;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business service for subject management.
 * Acts as the middle layer between the UI and {@link SubjectDAO},
 * enforcing business rules before any database access.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class SubjectService {

    /** DAO used to interact with the SUBJECT table. */
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Returns all registered subjects.
     *
     * @return list of all subjects (may be empty, never {@code null})
     */
    public List<Subject> findAll() {
        return subjectDAO.findAll();
    }

    /**
     * Finds a subject by its primary key.
     *
     * @param id the subject identifier
     * @return the matching {@link Subject}
     * @throws SubjectNotFoundException if no subject exists with that id
     */
    public Subject findById(int id) throws SubjectNotFoundException {
        Subject s = subjectDAO.findById(id);
        if (s == null) throw new SubjectNotFoundException(id);
        return s;
    }

    /**
     * Saves a new subject after validating name and course.
     *
     * @param subject the subject to create; must not be {@code null}
     * @throws IllegalArgumentException if name is blank or course is not 1 or 2
     */
    public void save(Subject subject) {
        validateSubject(subject);
        subjectDAO.save(subject);
    }

    /**
     * Updates an existing subject record.
     *
     * @param subject the subject with updated values; must not be {@code null}
     * @throws SubjectNotFoundException if the subject does not exist
     * @throws IllegalArgumentException if validation fails
     */
    public void update(Subject subject) throws SubjectNotFoundException {
        findById(subject.getId());
        validateSubject(subject);
        subjectDAO.update(subject);
    }

    /**
     * Deletes a subject by ID.
     *
     * @param id the subject ID to delete
     * @throws SubjectNotFoundException if the subject does not exist
     */
    public void deleteSubject(int id) throws SubjectNotFoundException {
        findById(id);
        subjectDAO.deleteById(id);
    }

    /**
     * Returns all subjects sorted by name.
     *
     * @return sorted list of subjects
     */
    public List<Subject> findAllSortedByName() {
        return subjectDAO.findAll().stream()
                .sorted(Comparator.comparing(Subject::getName))
                .collect(Collectors.toList());
    }

    /**
     * Returns subjects belonging to a specific course year.
     *
     * @param course the course year to filter by (1 or 2)
     * @return list of matching subjects
     */
    public List<Subject> findByCourse(int course) {
        return subjectDAO.findAll().stream()
                .filter(s -> s.getCourse() == course)
                .collect(Collectors.toList());
    }

    /**
     * Returns the names of all subjects.
     *
     * @return list of subject names
     */
    public List<String> getAllSubjectNames() {
        return subjectDAO.findAll().stream()
                .map(Subject::getName)
                .collect(Collectors.toList());
    }

    /**
     * Validates that the subject has a non-blank name and a course of 1 or 2.
     *
     * @param s the subject to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSubject(Subject s) {
        if (s.getName() == null || s.getName().isBlank())
            throw new IllegalArgumentException("Subject name cannot be blank.");
        if (s.getCourse() < 1 || s.getCourse() > 2)
            throw new IllegalArgumentException("Course must be 1 or 2.");
    }
}
