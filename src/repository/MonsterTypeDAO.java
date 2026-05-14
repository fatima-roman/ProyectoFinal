package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.MonsterType;
import util.DatabaseConnection;

/**
 * DAO for MonsterType persistence using JDBC + SQLite.
 *
 * @author Fatima R
 * @version 1.0
 */
public class MonsterTypeDAO extends GenericRepositoryBD<MonsterType> {

    /**
     * Inserts a new MonsterType record into the database.
     *
     * @param mt the MonsterType to save
     */
    @Override
    public void save(MonsterType mt) {
        String sql = "INSERT INTO MONSTER_TYPE(id, name, description, weakness, terrorLevel) " +
                     "VALUES(?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mt.getId());
            ps.setString(2, mt.getName());
            ps.setString(3, mt.getDescription());
            ps.setString(4, mt.getWeakness());
            ps.setInt(5, mt.getTerrorLevel());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MonsterTypeDAO.save] " + e.getMessage());
        }
    }

    /**
     * Updates an existing MonsterType record in the database.
     *
     * @param mt the MonsterType with updated data
     */
    @Override
    public void update(MonsterType mt) {
        String sql = "UPDATE MONSTER_TYPE SET name=?, description=?, weakness=?, terrorLevel=? " +
                     "WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mt.getName());
            ps.setString(2, mt.getDescription());
            ps.setString(3, mt.getWeakness());
            ps.setInt(4, mt.getTerrorLevel());
            ps.setInt(5, mt.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MonsterTypeDAO.update] " + e.getMessage());
        }
    }

    /**
     * Deletes the MonsterType with the given ID.
     *
     * @param id the ID of the MonsterType to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM MONSTER_TYPE WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MonsterTypeDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Retrieves a MonsterType by its ID.
     *
     * @param id the ID to search for
     * @return the found MonsterType, or {@code null} if not present
     */
    @Override
    public MonsterType findById(int id) {
        String sql = "SELECT * FROM MONSTER_TYPE WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MonsterTypeDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all MonsterType records from the database.
     *
     * @return list of all monster types
     */
    @Override
    public List<MonsterType> findAll() {
        List<MonsterType> list = new ArrayList<>();
        String sql = "SELECT * FROM MONSTER_TYPE";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[MonsterTypeDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link MonsterType} object.
     *
     * @param rs the current ResultSet row
     * @return populated MonsterType instance
     * @throws SQLException if any column cannot be read
     */
    private MonsterType mapRow(ResultSet rs) throws SQLException {
        return new MonsterType(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("weakness"),
            rs.getInt("terrorLevel")
        );
    }
}
