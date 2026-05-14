package model;

import java.util.Objects;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents an academic subject taught at Monster High Institute.
 * @author Fatima
 * @version 1.0
 */
public class Subject implements Identifiable, Exportable {

    private int id;
    private String name;
    private int course;
    private Teacher teacher;

    public Subject(int id, String name, int course, Teacher teacher) {
        this.id      = id;
        this.name    = name;
        this.course  = course;
        this.teacher = teacher;
    }

    @Override public int getId()            { return id; }
    @Override public void setId(int id)     { this.id = id; }
    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }
    public int getCourse()                  { return course; }
    public void setCourse(int course)       { this.course = course; }
    public Teacher getTeacher()             { return teacher; }
    public void setTeacher(Teacher t)       { this.teacher = t; }

    public void assignTeacher(Teacher t) { this.teacher = t; }

    @Override
    public String toCsv() {
        int tid = (teacher != null) ? teacher.getId() : -1;
        return id + "," + name + "," + course + "," + tid;
    }

    @Override
    public String toString() {
        String tName = (teacher != null) ? teacher.getName() + " " + teacher.getSurname() : "none";
        return "Subject[id=" + id + ", name=" + name + ", course=" + course + ", teacher=" + tName + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        return id == ((Subject) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}