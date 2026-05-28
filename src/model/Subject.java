package model;

import java.util.Objects;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents an academic subject taught at Monster High Institute.
 * Implements {@link Identifiable} for generic repository support and
 * {@link Exportable} for CSV export.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class Subject implements Identifiable, Exportable {

    private int id;
    private String name;
    private int course;
    private Teacher teacher;


    public Subject(int id, String name, int course, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.teacher = teacher;
    }

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCourse() { return course; }
    public void setCourse(int course) { this.course = course; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher t) { this.teacher = t; }

    /**
     * Assigns a teacher to this subject (alias for {@link #setTeacher(Teacher)}).
     *
     * @param t teacher to assign
     */
    public void assignTeacher(Teacher t) { this.teacher = t; }

    /**
     * Exports this subject as a CSV line: id,name,course,teacherId.
     *
     * @return CSV representation
     */
    @Override
    public String toCsv() {
        int tid = (teacher != null) ? teacher.getId() : -1;
        return id + "," + name + "," + course + "," + tid;
    }

    /**
     * Returns a human-readable representation of this subject.
     *
     * @return string with id, name, course and teacher name
     */
    @Override
    public String toString() {
        String tName = (teacher != null) ? teacher.getName() + " " + teacher.getSurname() : "N/A";
        return  "\n" +
                " ID:          " + id + "\n" +
                " Name:        " + name + "\n" +
                " Course:      " + course + "\n" +
                " Teacher:     " + tName + "\n" +
                "─────────────────────────────────────";
    }

    /**
     * Two subjects are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        return id == ((Subject) o).id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(id); }
}
