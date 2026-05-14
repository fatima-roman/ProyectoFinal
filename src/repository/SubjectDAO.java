package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Subject;
import model.Teacher;
import util.DatabaseConnection;

/**
 * DAO for Subject persistence using JDBC + SQLite.
 *
 * @author Fatima
 * @version 1.0
 */
public class SubjectDAO extends GenericRepositoryBD<Subject> {

    /** Used internally to resolve the Teacher foreign key. */
    private final TeacherDAO teacherDAO = new TeacherDAO();

    /**
     * Inserts a new Subject record into the database.
     *
     * @param s the Subject to save
     */
    @Override
    public void save(Subject s) {
        String sql = "INSERT INTO SUBJECT(id, name, course, teacherId) VALUES(?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getId());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getCourse());
            if (s.getTeacher() != null) ps.setInt(4, s.getTeacher().getId());
            else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SubjectDAO.save] " + e.getMessage());
        }
    }

    /**
     * Updates an existing Subject record in the database.
     *
     * @param s the Subject with updated data
     */
    @Override
    public void update(Subject s) {
        String sql = "UPDATE SUBJECT SET name=?, course=?, teacherId=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setInt(2, s.getCourse());
            if (s.getTeacher() != null) ps.setInt(3, s.getTeacher().getId());
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SubjectDAO.update] " + e.getMessage());
        }
    }

    /**
     * Deletes the Subject with the given ID.
     *
     * @param id the ID of the Subject to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM SUBJECT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SubjectDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Retrieves a Subject by its ID.
     *
     * @param id the ID to search for
     * @return the found Subject, or {@code null} if not present
     */
    @Override
    public Subject findById(int id) {
        String sql = "SELECT * FROM SUBJECT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[SubjectDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all Subject records from the database.
     *
     * @return list of all subjects
     */
    @Override
    public List<Subject> findAll() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM SUBJECT";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[SubjectDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link Subject} object.
     * Resolves the Teacher FK via {@link TeacherDAO}.
     *
     * @param rs the current ResultSet row
     * @return populated Subject instance
     * @throws SQLException if any column cannot be read
     */
    private Subject mapRow(ResultSet rs) throws SQLException {
        int teacherId = rs.getInt("teacherId");
        Teacher teacher = rs.wasNull() ? null : teacherDAO.findById(teacherId);
        return new Subject(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("course"),
            teacher
        );
    }
}
