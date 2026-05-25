package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Teacher;
import util.DatabaseConnection;

/**
 * DAO for Teacher persistence using JDBC + SQLite.
 *
 * @author Fatima R
 * @version 1.0
 */
public class TeacherDAO extends GenericRepositoryBD<Teacher> {

    /**
     * Inserts a new Teacher record into the database.
     *
     * @param t the Teacher to save
     */
    @Override
    public void save(Teacher t) {
        String sql = "INSERT INTO TEACHER(name, surname, birthDate, email, specialty) " +
                     "VALUES(?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getSurname());
            ps.setString(3, t.getBirthDate() != null ? t.getBirthDate().toString() : null);
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getSpecialty());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TeacherDAO.save] " + e.getMessage());
        }
    }

    /**
     * Updates an existing Teacher record in the database.
     *
     * @param t the Teacher with updated data
     */
    @Override
    public void update(Teacher t) {
        String sql = "UPDATE TEACHER SET name=?, surname=?, birthDate=?, email=?, specialty=? " +
                     "WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getSurname());
            ps.setString(3, t.getBirthDate() != null ? t.getBirthDate().toString() : null);
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getSpecialty());
            ps.setInt(6, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TeacherDAO.update] " + e.getMessage());
        }
    }

    /**
     * Deletes the Teacher with the given ID.
     *
     * @param id the ID of the Teacher to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM TEACHER WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TeacherDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Retrieves a Teacher by its ID.
     *
     * @param id the ID to search for
     * @return the found Teacher, or {@code null} if not present
     */
    @Override
    public Teacher findById(int id) {
        String sql = "SELECT * FROM TEACHER WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[TeacherDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all Teacher records from the database.
     *
     * @return list of all teachers
     */
    @Override
    public List<Teacher> findAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM TEACHER";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TeacherDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link Teacher} object.
     *
     * @param rs the current ResultSet row
     * @return populated Teacher instance
     * @throws SQLException if any column cannot be read
     */
    private Teacher mapRow(ResultSet rs) throws SQLException {
        String bd = rs.getString("birthDate");
        return new Teacher(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("surname"),
            bd != null ? LocalDate.parse(bd) : null,
            rs.getString("email"),
            rs.getString("specialty")
        );
    }
}
