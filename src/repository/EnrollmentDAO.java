package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Enrollment;
import model.Student;
import model.Subject;
import util.DatabaseConnection;

/**
 * Data Access Object for persisting {@link Enrollment} entities using JDBC + SQLite.
 * Extends {@link GenericRepositoryBD} with the {@link Enrollment} type.
 * @author Fatima
 * @version 1.1
 */
public class EnrollmentDAO extends GenericRepositoryBD<Enrollment> {
    private final StudentDAO studentDAO = new StudentDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Inserts a new enrollment record into the database.
     * @param e the enrollment to save
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void save(Enrollment e) {
        String sql = "INSERT INTO ENROLLMENT(id, studentId, subjectId, grade1, grade2) " +
                     "VALUES(?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, e.getId());
            ps.setInt(2, e.getStudent().getId());
            ps.setInt(3, e.getSubject().getId());
            ps.setDouble(4, e.getGrade1());
            ps.setDouble(5, e.getGrade2());
            ps.executeUpdate();
        } catch (SQLException ex) {
            //F: rethrow so the caller knows the operation failed
            throw new RuntimeException("[EnrollmentDAO.save] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Updates grade1 and grade2 for an existing enrollment.
     * @param e the enrollment with updated grades (must carry a valid id)
     * @throws RuntimeException if a database error occurs
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
            throw new RuntimeException("[EnrollmentDAO.update] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Deletes the enrollment with the given id.
     * @param id the id of the enrollment to delete
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM ENROLLMENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("[EnrollmentDAO.deleteById] Database error: " + ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves an enrollment by its id.
     * @param id the id to look up
     * @return the matching enrollment, or {@code null} if none exists with that id
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public Enrollment findById(int id) {
        String sql = "SELECT * FROM ENROLLMENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EnrollmentDAO.findById] Database error: " + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Returns all enrollment records stored in the database.
     * @return list of all enrollments (empty if none exist)
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public List<Enrollment> findAll() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM ENROLLMENT";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("[EnrollmentDAO.findAll] Database error: " + ex.getMessage(), ex);
        }
        return list;
    }

    /**
     * Retrieves all enrollments belonging to a specific student.
     * @param studentId the id of the student whose enrollments to fetch
     * @return list of matching enrollments (empty if none found)
     * @throws RuntimeException if a database error occurs
     */
    public List<Enrollment> findByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM ENROLLMENT WHERE studentId=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EnrollmentDAO.findByStudentId] Database error: " + ex.getMessage(), ex);
        }
        return list;
    }

    /**
     * Checks whether a student is already enrolled in a given subject.
     * Used by {@link service.StudentService} to prevent duplicate enrollments.
     * This query runs against the database, so it is always accurate regardless
     * of whether the in-memory Student object has its enrollment list populated.
     * @param studentId the id of the student to check
     * @param subjectId the id of the subject to check
     * @return {@code true} if an enrollment already exists for that student/subject pair
     * @throws RuntimeException if a database error occurs
     */
    public boolean existsByStudentAndSubject(int studentId, int subjectId) {
        String sql = "SELECT COUNT(*) FROM ENROLLMENT WHERE studentId=? AND subjectId=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EnrollmentDAO.existsByStudentAndSubject] Database error: " + ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * Maps the current row of a {@link ResultSet} into an {@link Enrollment} object.
     * Resolves the Student and Subject foreign keys via their respective DAOs.
     * @param rs the current ResultSet row
     * @return the Enrollment built from that row
     * @throws SQLException if any column cannot be read
     */
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Student student = studentDAO.findById(rs.getInt("studentId"));
        Subject subject = subjectDAO.findById(rs.getInt("subjectId"));
        return new Enrollment(
            rs.getInt("id"),
            student,
            subject,
            rs.getDouble("grade1"),
            rs.getDouble("grade2")
        );
    }
}