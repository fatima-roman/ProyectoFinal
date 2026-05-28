package model;

import java.util.Objects;
import model.interfaces.Evaluable;
import model.interfaces.Exportable;
import model.interfaces.Identifiable;

/**
 * Represents the enrollment of a {@link Student} in a {@link Subject},
 * storing the two partial grades obtained.
 *
 * @author Fatima
 * @version 1.1
 */
public class Enrollment implements Identifiable, Evaluable, Exportable {
    private int id;
    private Student student;
    private Subject subject;
    private double grade1;
    private double grade2;

    public Enrollment(int id, Student student, Subject subject, double grade1, double grade2) {
        this.id      = id;
        this.student = student;
        this.subject = subject;
        this.grade1  = grade1;
        this.grade2  = grade2;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student s) { this.student = s; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject s) { this.subject = s; }
    public double getGrade1() { return grade1; }
    public void setGrade1(double g) { this.grade1 = g; }
    public double getGrade2() { return grade2; }
    public void setGrade2(double g) { this.grade2 = g; }

    /**
     * Returns the computed final grade.
     * Delegates to {@link #calculateFinalGrade()} to ensure the value
     * is always up to date and never stale.
     *
     * @return the average of grade1 and grade2
     */
    public double getFinalGrade() { return calculateFinalGrade(); }

    /**
     * Calculates the final grade as the average of both partial grades.
     * @return (grade1 + grade2) / 2
     */
    @Override
    public double calculateFinalGrade() { return (grade1 + grade2) / 2.0; }

    /**
     * Returns whether the student passed this subject (final grade >= 5).
     * @return {@code true} if the final grade is 5 or above
     */
    @Override
    public boolean hasPassed() { return calculateFinalGrade() >= 5.0; }

    /**
     * Exports the enrollment as a CSV line with the following fields:
     * id, studentId, subjectId, grade1, grade2, finalGrade.
     * @return CSV line representing this enrollment
     */
    @Override
    public String toCsv() {
        return id + "," + student.getId() + "," + subject.getId() +
               "," + grade1 + "," + grade2 + "," + calculateFinalGrade();
    }

    @Override
    public String toString() {
        return  "\n" +
                " ┌─ Enrollment #" + id + "\n" +
                " │ Student  » " + student.getName() + " " + student.getSurname() + "\n" +
                " │ Subject  » " + subject.getName() + "\n" +
                " └ Grade    » " + String.format("%.2f", calculateFinalGrade()) + "\n";
    }

    /**
     * Two enrollments are considered equal if they share the same {@code id}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enrollment)) return false;
        return id == ((Enrollment) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}