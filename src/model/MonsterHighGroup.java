package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents an academic group (class) at Monster High Institute.
 * Each group has a name, a list of enrolled students and an optional tutor.
 * Implements {@link Identifiable} and {@link Exportable}.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class MonsterHighGroup implements Identifiable, Exportable {

    /** Unique identifier of the group. */
    private int id;

    /** Group name (e.g. "1A"). */
    private String name;

    /** Students belonging to this group. */
    private List<Student> students;

    /** Teacher acting as tutor of this group; may be {@code null}. */
    private Teacher tutor;

    /**
     * Constructs a MonsterHighGroup with the given id, name and tutor.
     *
     * @param id    unique identifier
     * @param name  group name
     * @param tutor tutor teacher, or {@code null}
     */
    public MonsterHighGroup(int id, String name, Teacher tutor) {
        this.id = id;
        this.name = name;
        this.tutor = tutor;
        this.students = new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public int getId() { return id; }

    /** {@inheritDoc} */
    @Override
    public void setId(int id) { this.id = id; }

    /**
     * Returns the group name.
     *
     * @return group name
     */
    public String getName() { return name; }

    /**
     * Sets the group name.
     *
     * @param name new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the students in this group.
     *
     * @return student list
     */
    public List<Student> getStudents() { return students; }

    /**
     * Sets the full student list.
     *
     * @param s new student list
     */
    public void setStudents(List<Student> s) { this.students = s; }

    /**
     * Returns the tutor of this group.
     *
     * @return tutor teacher, or {@code null}
     */
    public Teacher getTutor() { return tutor; }

    /**
     * Sets the tutor of this group.
     *
     * @param t new tutor
     */
    public void setTutor(Teacher t) { this.tutor = t; }

    /**
     * Adds a student to this group if not already present.
     *
     * @param s student to add
     */
    public void addStudent(Student s) {
        if (!students.contains(s)) students.add(s);
    }

    /**
     * Removes a student from this group.
     *
     * @param s student to remove
     */
    public void removeStudent(Student s) { students.remove(s); }

    /**
     * Exports this group as a CSV line: id,name,tutorId.
     *
     * @return CSV representation
     */
    @Override
    public String toCsv() {
        int tid = (tutor != null) ? tutor.getId() : -1;
        return id + "," + name + "," + tid;
    }

    /**
     * Returns a human-readable representation of this group.
     *
     * @return string with id, name, student count and tutor surname
     */
    @Override
    public String toString() {
        String tName = (tutor != null) ? tutor.getSurname() : "none";
        return "MonsterHighGroup[id=" + id + ", name=" + name +
               ", students=" + students.size() + ", tutor=" + tName + "]";
    }

    /**
     * Two groups are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonsterHighGroup)) return false;
        return id == ((MonsterHighGroup) o).id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(id); }
}
