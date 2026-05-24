package repository;

import exceptions.DatabaseException;
import model.Enrollment;
import model.Student;
import model.Subject;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for persisting {@link Enrollment} entities using JDBC + SQLite.
 * Extends {@link GenericRepositoryBD} with the {@link Enrollment} type.
 *
 * <p>This DAO uses a two-phase mapping approach in list queries:
 * raw scalar values are collected while the {@link ResultSet} is open,
 * and related {@link Student} and {@link Subject} objects are resolved only after
 * the result set has been closed. This avoids SQLite errors such as
 * "stmt pointer is closed" caused by nested DAO calls while iterating rows.</p>
 *
 * @author Fatima Roman
 * @version 1.2
 */
public class EnrollmentDAO extends GenericRepositoryBD<Enrollment> {

    /** DAO used to resolve student foreign keys. */
    private final StudentDAO studentDAO = new StudentDAO();

    /** DAO used to resolve subject foreign keys. */
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Inserts a new enrollment record into the database.
     * The {@code id} field is ignored because the database assigns it automatically.
     *
     * @param e the enrollment to save; must not be {@code null}
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public void save(Enrollment e) {
        String sql = "INSERT INTO ENROLLMENT(studentId, subjectId, grade1, grade2) VALUES(?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, e.getStudent().getId());
            ps.setInt(2, e.getSubject().getId());
            ps.setDouble(3, e.getGrade1());
            ps.setDouble(4, e.getGrade2());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.save] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Updates grade1 and grade2 for an existing enrollment.
     *
     * @param e the enrollment with updated grades; must carry a valid id
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public void update(Enrollment e) {
        String sql = "UPDATE ENROLLMENT SET grade1=?, grade2=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, e.getGrade1());
            ps.setDouble(2, e.getGrade2());
            ps.setInt(3, e.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.update] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deletes the enrollment with the given id.
     *
     * @param id the id of the enrollment to delete
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM ENROLLMENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.deleteById] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves an enrollment by its id.
     *
     * @param id the id to look up
     * @return the matching enrollment, or {@code null} if none exists with that id
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public Enrollment findById(int id) {
        String sql = "SELECT id, studentId, subjectId, grade1, grade2 FROM ENROLLMENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                int enrollmentId = rs.getInt("id");
                int studentId = rs.getInt("studentId");
                int subjectId = rs.getInt("subjectId");
                double grade1 = rs.getDouble("grade1");
                double grade2 = rs.getDouble("grade2");

                Student student = studentDAO.findById(studentId);
                Subject subject = subjectDAO.findById(subjectId);
                return new Enrollment(enrollmentId, student, subject, grade1, grade2);
            }
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.findById] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns all enrollment records stored in the database.
     *
     * @return list of all enrollments (empty if none exist)
     * @throws DatabaseException if a database error occurs
     */
    @Override
    public List<Enrollment> findAll() {
        List<Enrollment> list = new ArrayList<>();
        List<EnrollmentRow> rawRows = new ArrayList<>();
        String sql = "SELECT id, studentId, subjectId, grade1, grade2 FROM ENROLLMENT";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rawRows.add(new EnrollmentRow(
                        rs.getInt("id"),
                        rs.getInt("studentId"),
                        rs.getInt("subjectId"),
                        rs.getDouble("grade1"),
                        rs.getDouble("grade2")
                ));
            }
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.findAll] Database error: " + ex.getMessage(), ex);
        }

        for (EnrollmentRow row : rawRows) {
            Student student = studentDAO.findById(row.studentId());
            Subject subject = subjectDAO.findById(row.subjectId());
            list.add(new Enrollment(row.id(), student, subject, row.grade1(), row.grade2()));
        }
        return list;
    }

    /**
     * Retrieves all enrollments belonging to a specific student.
     *
     * @param studentId the id of the student whose enrollments to fetch
     * @return list of matching enrollments (empty if none found)
     * @throws DatabaseException if a database error occurs
     */
    public List<Enrollment> findByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        List<EnrollmentRow> rawRows = new ArrayList<>();
        String sql = "SELECT id, studentId, subjectId, grade1, grade2 FROM ENROLLMENT WHERE studentId=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rawRows.add(new EnrollmentRow(
                            rs.getInt("id"),
                            rs.getInt("studentId"),
                            rs.getInt("subjectId"),
                            rs.getDouble("grade1"),
                            rs.getDouble("grade2")
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.findByStudentId] Database error: " + ex.getMessage(), ex);
        }

        for (EnrollmentRow row : rawRows) {
            Student student = studentDAO.findById(row.studentId());
            Subject subject = subjectDAO.findById(row.subjectId());
            list.add(new Enrollment(row.id(), student, subject, row.grade1(), row.grade2()));
        }
        return list;
    }

    /**
     * Checks whether a student is already enrolled in a given subject.
     * Used by the service layer to prevent duplicate enrollments.
     *
     * @param studentId the id of the student to check
     * @param subjectId the id of the subject to check
     * @return {@code true} if an enrollment already exists for that student/subject pair
     * @throws DatabaseException if a database error occurs
     */
    public boolean existsByStudentAndSubject(int studentId, int subjectId) {
        String sql = "SELECT COUNT(*) FROM ENROLLMENT WHERE studentId=? AND subjectId=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new DatabaseException("[EnrollmentDAO.existsByStudentAndSubject] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Immutable helper record used to store raw enrollment data while the
     * {@link ResultSet} is still open.
     *
     * @param id        enrollment id
     * @param studentId referenced student id
     * @param subjectId referenced subject id
     * @param grade1    first partial grade
     * @param grade2    second partial grade
     */
    private record EnrollmentRow(int id, int studentId, int subjectId, double grade1, double grade2) {
    }
}
