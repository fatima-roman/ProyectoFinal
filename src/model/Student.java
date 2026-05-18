package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.interfaces.Evaluable;
import model.interfaces.Exportable;

/**
 * Represents a student of the Monster High Institute.
 * Extends {@link Person} and implements {@link Evaluable} and {@link Exportable}.
 *
 * @author Fatima
 * @version 1.1
 */
public class Student extends Person implements Exportable, Evaluable {
    private int studentYear;
    private String groupName;
    private MonsterType monsterType;
    private List<Enrollment> enrollments;

    public Student(int id, String name, String surname, LocalDate birthDate,
                   String email, int studentYear, String groupName, MonsterType monsterType) {
        super(id, name, surname, birthDate, email);
        this.studentYear = studentYear;
        this.groupName   = groupName;
        this.monsterType = monsterType;
        this.enrollments = new ArrayList<>();
    }

    public Student(Student copy) {
        super(copy.id, copy.name, copy.surname, copy.birthDate, copy.email);
        this.studentYear = copy.studentYear;
        this.groupName   = copy.groupName;
        this.monsterType = copy.monsterType;
        this.enrollments = new ArrayList<>(copy.enrollments);
    }

    //Getters & setters

    public int getStudentYear() { return studentYear; }
    public void setStudentYear(int y) {
        if (y < 1 || y > 2)
            throw new IllegalArgumentException("Student year must be 1 or 2, got: " + y);
        this.studentYear = y;
    }

    public void setGroupName(String g) {
        if (g == null || g.isBlank())
            throw new IllegalArgumentException("Group name cannot be blank.");
        this.groupName = g;
    }    
    public String getGroupName() { return groupName; }
    public MonsterType getMonsterType() { return monsterType; }
    public void setMonsterType(MonsterType m) { this.monsterType = m; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> e) { this.enrollments = e; }


    /**
     * Adds an enrollment to this student's list.
     * @param enrollment the enrollment to add
     */
    public void enrollSubject(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    /**
     * Removes the enrollment for the given subject.
     * @param subject the subject to unenroll from
     */
    public void removeEnrollment(Subject subject) {
        enrollments.removeIf(e -> e.getSubject().equals(subject));
    }


    /**
     * Calculates the student's average grade across all enrollments.
     * @return average of all final grades, or 0.0 if there are no enrollments
     */
    @Override
    public double calculateFinalGrade() {
        return enrollments.stream()
            .mapToDouble(Enrollment::calculateFinalGrade)
            .average().orElse(0.0);
    }

    /**
     * Returns whether the student has passed (average grade >= 5).
     * @return {@code true} if the average grade is 5 or above
     */
    @Override
    public boolean hasPassed() { return calculateFinalGrade() >= 5.0; }

    /**
     * Returns a human-readable role description.
     *
     * @return string with the student's year and group
     */
    @Override
    public String getRoleDescription() {
        return "Student – Year " + studentYear + ", Group " + groupName;
    }

    /**
     * Exports the student as a CSV line.
     * Fields that may contain commas are wrapped in double quotes.
     * A {@code null} birthDate is exported as an empty string (not the literal "null").
     * @return CSV line with the student's data
     */
    @Override
    public String toCsv() {
        // FIX: escape fields that may contain commas; handle null birthDate properly
        int mid = (monsterType != null) ? monsterType.getId() : -1;
        String bd = (birthDate != null) ? birthDate.toString() : "";
        return escapeCsv(id)       + "," + escapeCsv(name)      + "," +
               escapeCsv(surname)  + "," + bd                   + "," +
               escapeCsv(email)    + "," + studentYear           + "," +
               escapeCsv(groupName) + "," + mid;
    }

    /**
     * Escapes a value for CSV format.
     * Wraps the value in double quotes if it contains commas, quotes, or newlines,
     * and doubles any internal quote characters.
     * @param value the object to escape (converted via {@code toString()})
     * @return a CSV-safe string, or an empty string if {@code value} is {@code null}
     */
    private String escapeCsv(Object value) {
        if (value == null) return "";
        String s = value.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }


    @Override
    public String toString() {
        return  "\n" +
                " ID:         " + id + "\n" +
                " Name:       " + name + " " + surname + "\n" +
                " Birth date: " + (birthDate != null ? birthDate : "N/A") + "\n" +
                " Email:      " + email + "\n" +
                " Year/Group: " + studentYear + " / " + groupName + "\n" +
                " Type:       " + (monsterType != null ? monsterType.getName() : "Unknown") + "\n" +
                " Avg grade:  " + String.format("%.2f", calculateFinalGrade()) + "\n" +
                "─────────────────────────────────────";
    }

    /**
     * Two students are considered equal if they share the same {@code id}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return id == ((Student) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}