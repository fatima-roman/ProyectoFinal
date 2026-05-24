package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.interfaces.Exportable;

/**
 * Represents a teacher at Monster High Institute.
 * Extends {@link Person} and implements {@link Exportable}.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class Teacher extends Person implements Exportable {

    /** Teaching specialty of this teacher. */
    private String specialty;

    /** Subjects assigned to this teacher. */
    private List<Subject> subjects;

    /**
     * Constructs a Teacher with all fields.
     *
     * @param id        unique identifier
     * @param name      first name
     * @param surname   last name
     * @param birthDate date of birth
     * @param email     email address
     * @param specialty teaching specialty
     */
    public Teacher(int id, String name, String surname, LocalDate birthDate,
                   String email, String specialty) {
        super(id, name, surname, birthDate, email);
        this.specialty = specialty;
        this.subjects = new ArrayList<>();
    }

    /**
     * Returns the teaching specialty.
     *
     * @return specialty string
     */
    public String getSpecialty() { return specialty; }

    /**
     * Sets the teaching specialty.
     *
     * @param s new specialty
     */
    public void setSpecialty(String s) { this.specialty = s; }

    /**
     * Returns the list of subjects assigned to this teacher.
     *
     * @return list of subjects
     */
    public List<Subject> getSubjects() { return subjects; }

    /**
     * Sets the full list of subjects.
     *
     * @param s new subject list
     */
    public void setSubjects(List<Subject> s) { this.subjects = s; }

    /**
     * Assigns a subject to this teacher if not already assigned.
     *
     * @param s subject to assign
     */
    public void assignSubject(Subject s) {
        if (!subjects.contains(s)) subjects.add(s);
    }

    /**
     * Removes a subject from this teacher's list.
     *
     * @param s subject to remove
     */
    public void unassignSubject(Subject s) { subjects.remove(s); }

    /**
     * Returns a human-readable role description for this teacher.
     *
     * @return role description including specialty
     */
    @Override
    public String getRoleDescription() { return "Teacher - Specialty: " + specialty; }

    /**
     * Exports this teacher as a CSV line: id,name,surname,birthDate,email,specialty.
     *
     * @return CSV representation
     */
    @Override
    public String toCsv() {
        return id + "," + name + "," + surname + "," + birthDate + "," + email + "," + specialty;
    }

    /**
     * Returns a human-readable representation of this teacher.
     *
     * @return string with id, full name and specialty
     */
    @Override
    public String toString() {
        return "Teacher[id=" + id + ", name=" + name + " " + surname + ", specialty=" + specialty + "]";
    }

    /**
     * Two teachers are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        return id == ((Teacher) o).id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(id); }
}
