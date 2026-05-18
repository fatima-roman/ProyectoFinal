package service;

import exceptions.StudentNotFoundException;
import model.Subject;
import repository.SubjectDAO;
import java.util.List;

/**
 * Business service for subject management.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class SubjectService {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Returns all registered subjects.
     * @return list of all subjects (may be empty, never null)
     */
    public List<Subject> findAll() {
        return subjectDAO.findAll();
    }

    /**
     * Finds a subject by its primary key.
     * @param id the subject identifier
     * @return the matching Subject
     * @throws StudentNotFoundException if no subject exists with that id
     */
    public Subject findById(int id) throws StudentNotFoundException {
        Subject s = subjectDAO.findById(id);
        if (s == null) throw new StudentNotFoundException("Subject with ID " + id + " not found.");
        return s;
    }

    /**
     * Saves a new subject. Validates name and course are not empty/invalid.
     * @param subject the subject to create
     * @throws IllegalArgumentException if validation fails
     */
    public void save(Subject subject) {
        if (subject.getName() == null || subject.getName().isBlank())
            throw new IllegalArgumentException("Subject name cannot be blank.");
        if (subject.getCourse() < 1)
            throw new IllegalArgumentException("Course must be 1 or higher.");
        subjectDAO.save(subject);
    }

    /**
     * Updates an existing subject.
     * @param subject the subject with updated values
     * @throws StudentNotFoundException if the subject does not exist
     */
    public void update(Subject subject) throws StudentNotFoundException {
        findById(subject.getId());
        subjectDAO.update(subject);
    }

    /**
     * Deletes a subject by ID.
     * @param id the subject ID
     * @throws StudentNotFoundException if the subject does not exist
     */
    public void deleteById(int id) throws StudentNotFoundException {
        findById(id);
        subjectDAO.deleteById(id);
    }
}