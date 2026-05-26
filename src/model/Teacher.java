package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.interfaces.Buscable;
import model.interfaces.Exportable;

/**
 * Represents a teacher at the Monster High Institute.
 * <p>
 * Extends {@link Person} and implements {@link Exportable} and {@link Buscable}.
 * It maintains the list of subjects assigned to this teacher.
 * </p>
 *
 * @author Fátima Román
 * @version 1.2
 */
public class Teacher extends Person implements Exportable, Buscable {

    /** Teaching specialty of this teacher. */
    private String specialty;

    /** Subjects assigned to this teacher. */
    private List<Subject> subjects;

    /**
     * Full constructor.
     *
     * @param id        unique identifier
     * @param name      first name
     * @param surname   surname
     * @param birthDate birth date
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
     * Copy constructor.
     *
     * @param copy teacher to copy
     */
    public Teacher(Teacher copy) {
        super(copy.getId(), copy.getName(), copy.getSurname(),
              copy.getBirthDate(), copy.getEmail());
        this.specialty = copy.specialty;
        this.subjects  = new ArrayList<>(copy.subjects);
    }

    /**
     * Returns the teaching specialty.
     *
     * @return specialty
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
     * @return subject list
     */
    public List<Subject> getSubjects() { return subjects; }

    /**
     * Replaces the entire subject list.
     *
     * @param s new subject list
     */
    public void setSubjects(List<Subject> s) { this.subjects = s; }

    /**
     * Assigns a subject to this teacher if it is not already assigned.
     *
     * @param s subject to assign
     */
    public void assignSubject(Subject s) {
        if (!subjects.contains(s)) subjects.add(s);
    }

    /**
     * Removes the specified subject from this teacher's list.
     *
     * @param s subject to remove
     */
    public void unassignSubject(Subject s) { subjects.remove(s); }

    /**
     * Returns a description of this teacher's role.
     *
     * @return description including the specialty
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
        return getId() + "," + getName() + "," + getSurname() + ","
             + getBirthDate() + "," + getEmail() + "," + specialty;
    }

    /**
     * Returns a human-readable representation of this teacher.
     *
     * @return string with id, full name and specialty
     */
    @Override
    public String toString() {
        return "Teacher[id=" + getId() + ", name=" + getName() + " " + getSurname()
             + ", specialty=" + specialty + "]";
    }

    /**
     * Two teachers are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if the ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teacher)) return false;
        return getId() == ((Teacher) o).getId();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(getId()); }

    /**
     * Checks whether this teacher matches the given keyword by searching
     * in name, surname and specialty (case-insensitive).
     *
     * @param keyword search keyword
     * @return {@code true} if any field contains the keyword
     */
    @Override
    public boolean matches(String keyword) {
        if (keyword == null) return false;
        String k = keyword.toLowerCase();
        return getName().toLowerCase().contains(k)
            || getSurname().toLowerCase().contains(k)
            || specialty.toLowerCase().contains(k);
    }
}