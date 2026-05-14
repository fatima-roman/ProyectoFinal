package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.interfaces.Evaluable;
import model.interfaces.Exportable;

/**
 * Represents a student enrolled at Monster High Institute.
 * @author Fatima
 * @version 1.0
 */
public class Student extends Person implements Exportable, Evaluable {

    private int studentYear;
    private String groupName;
    private MonsterType monsterType;
    private List<Enrollment> enrollments;

    /** Full constructor */
    public Student(int id, String name, String surname, LocalDate birthDate,
                   String email, int studentYear, String groupName, MonsterType monsterType) {
        super(id, name, surname, birthDate, email);
        this.studentYear = studentYear;
        this.groupName   = groupName;
        this.monsterType = monsterType;
        this.enrollments = new ArrayList<>();
    }

    /** Copy constructor */
    public Student(Student copy) {
        super(copy.id, copy.name, copy.surname, copy.birthDate, copy.email);
        this.studentYear = copy.studentYear;
        this.groupName   = copy.groupName;
        this.monsterType = copy.monsterType;
        this.enrollments = new ArrayList<>(copy.enrollments);
    }

    public int getStudentYear()               { return studentYear; }
    public void setStudentYear(int y)         { this.studentYear = y; }
    public String getGroupName()              { return groupName; }
    public void setGroupName(String g)        { this.groupName = g; }
    public MonsterType getMonsterType()       { return monsterType; }
    public void setMonsterType(MonsterType m) { this.monsterType = m; }
    public List<Enrollment> getEnrollments()  { return enrollments; }
    public void setEnrollments(List<Enrollment> e) { this.enrollments = e; }

    public void enrollSubject(Enrollment enrollment) { enrollments.add(enrollment); }

    public void removeEnrollment(Subject subject) {
        enrollments.removeIf(e -> e.getSubject().equals(subject));
    }

    @Override
    public double calculateFinalGrade() {
        return enrollments.stream()
                .mapToDouble(Enrollment::calculateFinalGrade)
                .average().orElse(0.0);
    }

    @Override
    public boolean hasPassed() { return calculateFinalGrade() >= 5.0; }

    @Override
    public String getRoleDescription() {
        return "Student – Year " + studentYear + ", Group " + groupName;
    }

    @Override
    public String toCsv() {
        int mid = (monsterType != null) ? monsterType.getId() : -1;
        return id + "," + name + "," + surname + "," + birthDate +
               "," + email + "," + studentYear + "," + groupName + "," + mid;
    }

    @Override
    public String toString() {
        return "Student[id=" + id + ", name=" + name + " " + surname +
               ", year=" + studentYear + ", group=" + groupName +
               ", type=" + (monsterType != null ? monsterType.getName() : "none") + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return id == ((Student) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
