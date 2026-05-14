package service;

import java.util.*;
import java.util.stream.Collectors;
import exceptions.StudentNotFoundException;
import model.Subject;
import model.Teacher;
import repository.SubjectDAO;
import repository.TeacherDAO;

public class TeacherService {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    //CRUD

    public List<Teacher> findAll() {
        return teacherDAO.findAll();
    }

    public Teacher findById(int id) throws StudentNotFoundException {
        Teacher t = teacherDAO.findById(id);
        if (t == null) throw new StudentNotFoundException("Teacher with ID " + id + " was not found.");
        return t;
    }

    public void save(Teacher teacher) {
        validateTeacher(teacher);
        teacherDAO.save(teacher);
    }

    public void update(Teacher teacher) throws StudentNotFoundException {
        findById(teacher.getId());   // confirms it exists
        validateTeacher(teacher);
        teacherDAO.update(teacher);
    }

    public void deleteById(int id) throws StudentNotFoundException {
        findById(id);                // confirms it exists
        teacherDAO.deleteById(id);
    }

    //Subject assignment

    public void assignSubject(int teacherId, int subjectId) throws StudentNotFoundException {
        Teacher t = findById(teacherId);
        Subject s = subjectDAO.findById(subjectId);
        if (s == null) throw new StudentNotFoundException("Subject with ID " + subjectId + " not found.");
        t.assignSubject(s);
        s.assignTeacher(t);
        teacherDAO.update(t);
        subjectDAO.update(s);
    }

    public void unassignSubject(int teacherId, int subjectId) throws StudentNotFoundException {
        Teacher t = findById(teacherId);
        Subject s = subjectDAO.findById(subjectId);
        if (s == null) throw new StudentNotFoundException("Subject with ID " + subjectId + " not found.");
        t.unassignSubject(s);
        s.setTeacher(null);
        teacherDAO.update(t);
        subjectDAO.update(s);
    }

    //Streams

    /** Sorted by surname */
    public List<Teacher> findAllSortedBySurname() {
        return teacherDAO.findAll().stream()
                .sorted(Comparator.comparing(Teacher::getSurname))
                .collect(Collectors.toList());
    }

    /** Filter by specialty keyword */
    public List<Teacher> findBySpecialty(String keyword) {
        return teacherDAO.findAll().stream()
                .filter(t -> t.getSpecialty().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /** Teacher with most subjects */
    public Optional<Teacher> findBusiestTeacher() {
        return teacherDAO.findAll().stream()
                .max(Comparator.comparingInt(t -> t.getSubjects().size()));
    }

    /** Full names list */
    public List<String> getAllTeacherFullNames() {
        return teacherDAO.findAll().stream()
                .map(t -> t.getName() + " " + t.getSurname())
                .collect(Collectors.toList());
    }

    //Validation

    private void validateTeacher(Teacher t) {
        if (t.getName()      == null || t.getName().isBlank())
            throw new IllegalArgumentException("Teacher name cannot be blank.");
        if (t.getSurname()   == null || t.getSurname().isBlank())
            throw new IllegalArgumentException("Teacher surname cannot be blank.");
        if (t.getSpecialty() == null || t.getSpecialty().isBlank())
            throw new IllegalArgumentException("Teacher specialty cannot be blank.");
        if (t.getEmail()     == null || t.getEmail().isBlank())
            throw new IllegalArgumentException("Teacher email cannot be blank.");
    }
}
