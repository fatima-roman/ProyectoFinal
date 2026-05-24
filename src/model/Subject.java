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

    /** Unique identifier of the subject. */
    private int id;

    /** Name of the subject (e.g. "Dark Arts 101"). */
    private String name;

    /** Course year this subject belongs to (1 or 2). */
    private int course;

    /** Teacher assigned to this subject; may be {@code null}. */
    private Teacher teacher;

    /**
     * Constructs a Subject with all fields.
     *
     * @param id      unique identifier
     * @param name    subject name
     * @param course  course year (1 or 2)
     * @param teacher assigned teacher, or {@code null}
     */
    public Subject(int id, String name, int course, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.teacher = teacher;
    }

    /** {@inheritDoc} */
    @Override
    public int getId() { return id; }

    /** {@inheritDoc} */
    @Override
    public void setId(int id) { this.id = id; }

    /**
     * Returns the subject name.
     *
     * @return subject name
     */
    public String getName() { return name; }

    /**
     * Sets the subject name.
     *
     * @param name new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the course year.
     *
     * @return course year
     */
    public int getCourse() { return course; }

    /**
     * Sets the course year.
     *
     * @param course new course year
     */
    public void setCourse(int course) { this.course = course; }

    /**
     * Returns the teacher assigned to this subject.
     *
     * @return assigned teacher, or {@code null}
     */
    public Teacher getTeacher() { return teacher; }

    /**
     * Sets the teacher for this subject.
     *
     * @param t new teacher
     */
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
        String tName = (teacher != null) ? teacher.getName() + " " + teacher.getSurname() : "none";
        return "Subject[id=" + id + ", name=" + name + ", course=" + course + ", teacher=" + tName + "]";
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
