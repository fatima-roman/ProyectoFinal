package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Enrollment;
import model.Student;
import model.Subject;
import util.DatabaseConnection;

/**
 * DAO for Enrollment persistence using JDBC + SQLite.
 *
 * @author Fatima
 * @version 1.0
 */
public class EnrollmentDAO extends GenericRepositoryBD<Enrollment> {

    /** Used internally to resolve the Student foreign key. */
    private final StudentDAO studentDAO = new StudentDAO();

    /** Used internally to resolve the Subject foreign key. */
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /**
     * Inserts a new Enrollment record into the database.
     *
     * @param e the Enrollment to save
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
            System.err.println("[EnrollmentDAO.save] " + ex.getMessage());
        }
    }

    /**
     * Updates grade1 and grade2 for an existing Enrollment.
     *
     * @param e the Enrollment with updated grades
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
            System.err.println("[EnrollmentDAO.update] " + ex.getMessage());
        }
    }

    /**
     * Deletes the Enrollment with the given ID.
     *
     * @param id the ID of the Enrollment to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM ENROLLMENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[EnrollmentDAO.deleteById] " + ex.getMessage());
        }
    }

    /**
     * Retrieves an Enrollment by its ID.
     *
     * @param id the ID to search for
     * @return the found Enrollment, or {@code null} if not present
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
            System.err.println("[EnrollmentDAO.findById] " + ex.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all Enrollment records from the database.
     *
     * @return list of all enrollments
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
            System.err.println("[EnrollmentDAO.findAll] " + ex.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all enrollments for a specific student ID.
     *
     * @param studentId the student whose enrollments to fetch
     * @return list of matching enrollments
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
            System.err.println("[EnrollmentDAO.findByStudentId] " + ex.getMessage());
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to an {@link Enrollment} object.
     * Resolves Student and Subject FKs via their respective DAOs.
     *
     * @param rs the current ResultSet row
     * @return populated Enrollment instance
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
