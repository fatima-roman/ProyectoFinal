package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.interfaces.Buscable;
import model.interfaces.Evaluable;
import model.interfaces.Exportable;

/**
 * Represents a student at the Monster High Institute.
 * <p>
 * Extends {@link Person} and implements {@link Evaluable}, {@link Exportable}
 * and {@link Buscable}. It keeps the list of enrollments ({@link Enrollment})
 * in which the student participates.
 * </p>
 *
 * @author Fátima Román
 * @version 1.3
 */
public class Student extends Person implements Exportable, Evaluable, Buscable {

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


    public int getStudentYear() { return studentYear; }
    /**
     * Sets the academic year.
     *
     * @param y academic year
     * @throws IllegalArgumentException if it is not 1 or 2
     */
    public void setStudentYear(int y) {
        if (y < 1 || y > 2)
            throw new IllegalArgumentException("The year must be 1 or 2, received: " + y);
        this.studentYear = y;
    }

    public String getGroupName() { return groupName; }

    /**
     * Sets the group name.
     *
     * @param g new group name
     * @throws IllegalArgumentException if it is empty or null
     */
    public void setGroupName(String g) {
        if (g == null || g.isBlank())
            throw new IllegalArgumentException("Group name cannot be empty.");
        this.groupName = g;
    }
    public MonsterType getMonsterType() { return monsterType; }
    public void setMonsterType(MonsterType m) { this.monsterType = m; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> e) { this.enrollments = e; }

    /**
     * Adds an enrollment to this student's list.
     *
     * @param enrollment the enrollment to add
     */
    public void enrollSubject(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    /**
     * Removes the enrollment corresponding to the specified subject.
     *
     * @param subject subject from which the student is unenrolled
     */
    public void removeEnrollment(Subject subject) {
        enrollments.removeIf(e -> e.getSubject().equals(subject));
    }

    /**
     * Calculates the student's average grade by averaging the final grades of all enrollments.
     *
     * @return average grade, or 0.0 if there are no enrollments
     */
    @Override
    public double calculateFinalGrade() {
        return enrollments.stream()
            .mapToDouble(Enrollment::calculateFinalGrade)
            .average().orElse(0.0);
    }

    /**
     * Indicates whether the student has passed (average grade &ge; 5).
     *
     * @return {@code true} if the average grade is 5 or higher
     */
    @Override
    public boolean hasPassed() { return calculateFinalGrade() >= 5.0; }

    /**
     * Returns a description of this student's role.
     *
     * @return description with year and group
     */
    @Override
    public String getRoleDescription() {
        return "Student – Year " + studentYear + ", Group " + groupName;
    }

    /**
     * Exports the student as a CSV line.
     * Fields that may contain commas are enclosed in double quotes.
     * A {@code null} birthDate is exported as an empty string.
     *
     * @return CSV line with the student's data
     */
    @Override
    public String toCsv() {
        int mid = (monsterType != null) ? monsterType.getId() : -1;
        String bd = (getBirthDate() != null) ? getBirthDate().toString() : "";
        return escapeCsv(getId())         + "," + escapeCsv(getName())     + "," +
               escapeCsv(getSurname())    + "," + bd                       + "," +
               escapeCsv(getEmail())      + "," + studentYear               + "," +
               escapeCsv(groupName)       + "," + mid;
    }

    /**
     * Escapes a value for CSV format.
     * Wraps the value in double quotes if it contains commas, quotes or line breaks,
     * duplicating internal quotes.
     *
     * @param value the object to escape (converted with {@code toString()})
     * @return safe CSV string, or an empty string if {@code value} is {@code null}
     */
    private String escapeCsv(Object value) {
        if (value == null) return "";
        String s = value.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /**
     * Returns a human-readable representation of the student with all fields.
     *
     * @return multiline string with the student's data
     */
    @Override
    public String toString() {
        return  "\n" +
                " ID:          " + getId() + "\n" +
                " Name:        " + getName() + " " + getSurname() + "\n" +
                " Birth date:  " + (getBirthDate() != null ? getBirthDate() : "N/A") + "\n" +
                " Email:       " + getEmail() + "\n" +
                " Year/Group:  " + studentYear + " / " + groupName + "\n" +
                " Type:        " + (monsterType != null ? monsterType.getName() : "Unknown") + "\n" +
                " Average:     " + String.format("%.2f", calculateFinalGrade()) + "\n" +
                "─────────────────────────────────────";
    }

    /**
     * Two students are equal if they share the same {@code id}.
     *
     * @param o object to compare
     * @return {@code true} if the ids match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return getId() == ((Student) o).getId();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() { return Objects.hash(getId()); }

    /**
     * Checks whether this student matches the given keyword
     * by searching in name, surname, email and group (case-insensitive).
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
            || getEmail().toLowerCase().contains(k)
            || groupName.toLowerCase().contains(k);
    }
}