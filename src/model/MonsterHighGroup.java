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

    private int id;
    private String name;
    private List<Student> students;
    private Teacher tutor;

    public MonsterHighGroup(int id, String name, Teacher tutor) {
        this.id = id;
        this.name = name;
        this.tutor = tutor;
        this.students = new ArrayList<>();
    }

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> s) { this.students = s; }
    public Teacher getTutor() { return tutor; }
    public void setTutor(Teacher t) { this.tutor = t; }
    
    /**
     * Add a student from this group.
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
        String tName = (tutor != null) ? tutor.getName() + " " + tutor.getSurname() : "N/A";
        return  "\n" +
                " ID:          " + id + "\n" +
                " Name:        " + name + "\n" +
                " Students:    " + students.size() + "\n" +
                " Tutor:       " + tName + "\n" +
                "─────────────────────────────────────";
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
