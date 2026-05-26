package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import exceptions.DatabaseException;
import model.Teacher;
import util.DatabaseConnection;

/**
 * DAO for the persistence of {@link Teacher} using JDBC + SQLite.
 * Extends {@link GenericRepositoryBD} to fulfill the generic repository requirement.
 *
 * @author Fátima Román
 * @version 1.2
 */
public class TeacherDAO extends GenericRepositoryBD<Teacher> {

    /**
     * Inserts a new {@link Teacher} record into the database.
     *
     * @param t the teacher to persist; must not be {@code null}
     * @throws DatabaseException if an SQL error occurs during insertion
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
            throw new DatabaseException("[TeacherDAO.save] " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing {@link Teacher} record in the database.
     *
     * @param t the teacher with updated values; must not be {@code null}
     * @throws DatabaseException if an SQL error occurs during the update
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
            throw new DatabaseException("[TeacherDAO.update] " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the {@link Teacher} with the given ID from the database.
     *
     * @param id the identifier of the teacher to delete
     * @throws DatabaseException if an SQL error occurs during deletion
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM TEACHER WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("[TeacherDAO.deleteById] " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a {@link Teacher} by its primary key.
     *
     * @param id the identifier to search for
     * @return the teacher found, or {@code null} if it does not exist
     * @throws DatabaseException if an SQL error occurs during the query
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
            throw new DatabaseException("[TeacherDAO.findById] " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Retrieves all {@link Teacher} records from the database.
     *
     * @return list of all teachers (may be empty, never {@code null})
     * @throws DatabaseException if an SQL error occurs during the query
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
            throw new DatabaseException("[TeacherDAO.findAll] " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link Teacher} object.
     *
     * @param rs the current ResultSet row; must not be {@code null}
     * @return populated {@link Teacher} instance
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