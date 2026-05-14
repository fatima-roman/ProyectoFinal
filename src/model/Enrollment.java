package model;

import java.util.Objects;
import model.interfaces.Evaluable;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents a student's enrolment in a subject, including grades.
 * @author Fatima
 * @version 1.0
 */
public class Enrollment implements Identifiable, Evaluable, Exportable {

    private int id;
    private Student student;
    private Subject subject;
    private double grade1;
    private double grade2;
    private double finalGrade;

    public Enrollment(int id, Student student, Subject subject, double grade1, double grade2) {
        this.id         = id;
        this.student    = student;
        this.subject    = subject;
        this.grade1     = grade1;
        this.grade2     = grade2;
        this.finalGrade = calculateFinalGrade();
    }

    @Override public int getId()            { return id; }
    @Override public void setId(int id)     { this.id = id; }
    public Student getStudent()             { return student; }
    public void setStudent(Student s)       { this.student = s; }
    public Subject getSubject()             { return subject; }
    public void setSubject(Subject s)       { this.subject = s; }
    public double getGrade1()               { return grade1; }
    public void setGrade1(double g)         { this.grade1 = g; this.finalGrade = calculateFinalGrade(); }
    public double getGrade2()               { return grade2; }
    public void setGrade2(double g)         { this.grade2 = g; this.finalGrade = calculateFinalGrade(); }
    public double getFinalGrade()           { return finalGrade; }

    @Override
    public double calculateFinalGrade() { return (grade1 + grade2) / 2.0; }

    @Override
    public boolean hasPassed() { return calculateFinalGrade() >= 5.0; }

    @Override
    public String toCsv() {
        return id + "," + student.getId() + "," + subject.getId() +
               "," + grade1 + "," + grade2 + "," + calculateFinalGrade();
    }

    @Override
    public String toString() {
        return "Enrollment[id=" + id + ", student=" + student.getSurname() +
               ", subject=" + subject.getName() + ", finalGrade=" + calculateFinalGrade() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;
        return id == ((Enrollment) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
