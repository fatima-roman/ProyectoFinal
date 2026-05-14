package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.interfaces.Exportable;

/**
 * Represents a teacher at Monster High Institute.
 * @author Fatima
 * @version 1.0
 */
public class Teacher extends Person implements Exportable {

    private String specialty;
    private List<Subject> subjects;

    public Teacher(int id, String name, String surname, LocalDate birthDate,
                   String email, String specialty) {
        super(id, name, surname, birthDate, email);
        this.specialty = specialty;
        this.subjects  = new ArrayList<>();
    }

    public String getSpecialty()             { return specialty; }
    public void setSpecialty(String s)       { this.specialty = s; }
    public List<Subject> getSubjects()       { return subjects; }
    public void setSubjects(List<Subject> s) { this.subjects = s; }

    public void assignSubject(Subject s)   { if (!subjects.contains(s)) subjects.add(s); }
    public void unassignSubject(Subject s) { subjects.remove(s); }

    @Override
    public String getRoleDescription() { return "Teacher – Specialty: " + specialty; }

    @Override
    public String toCsv() {
        return id + "," + name + "," + surname + "," + birthDate + "," + email + "," + specialty;
    }

    @Override
    public String toString() {
        return "Teacher[id=" + id + ", name=" + name + " " + surname + ", specialty=" + specialty + "]";
    }
}