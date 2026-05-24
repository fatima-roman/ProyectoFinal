package repository;

import exceptions.DatabaseException;
import model.MonsterType;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for {@link MonsterType} persistence using JDBC + SQLite.
 * Extends {@link GenericRepositoryBD} to fulfil the generic repository requirement.
 * All primary keys are managed by the database via AUTOINCREMENT.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class MonsterTypeDAO extends GenericRepositoryBD<MonsterType> {

    /**
     * Inserts a new {@link MonsterType} into the database.
     * The {@code id} field is ignored; the database assigns it automatically.
     *
     * @param mt the monster type to persist; must not be {@code null}
     * @throws DatabaseException if a SQL error occurs
     */
    @Override
    public void save(MonsterType mt) {
        String sql = "INSERT INTO MONSTER_TYPE(name, description, weakness, terrorLevel) VALUES(?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mt.getName());
            ps.setString(2, mt.getDescription());
            ps.setString(3, mt.getWeakness());
            ps.setInt(4, mt.getTerrorLevel());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) mt.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("[MonsterTypeDAO.save] " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing {@link MonsterType} record in the database.
     *
     * @param mt the monster type with updated values; must not be {@code null}
     * @throws DatabaseException if a SQL error occurs
     */
    @Override
    public void update(MonsterType mt) {
        String sql = "UPDATE MONSTER_TYPE SET name=?, description=?, weakness=?, terrorLevel=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mt.getName());
            ps.setString(2, mt.getDescription());
            ps.setString(3, mt.getWeakness());
            ps.setInt(4, mt.getTerrorLevel());
            ps.setInt(5, mt.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("[MonsterTypeDAO.update] " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the {@link MonsterType} with the given ID from the database.
     *
     * @param id the identifier of the monster type to delete
     * @throws DatabaseException if a SQL error occurs
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM MONSTER_TYPE WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("[MonsterTypeDAO.deleteById] " + e.getMessage(), e);
        }
    }

    /**
     * Finds a {@link MonsterType} by its primary key.
     *
     * @param id the identifier to look up
     * @return the matching monster type, or {@code null} if not found
     * @throws DatabaseException if a SQL error occurs
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
            throw new DatabaseException("[MonsterTypeDAO.findById] " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns all {@link MonsterType} records stored in the database.
     *
     * @return list of all monster types (may be empty, never {@code null})
     * @throws DatabaseException if a SQL error occurs
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
            throw new DatabaseException("[MonsterTypeDAO.findAll] " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Maps a single {@link ResultSet} row to a {@link MonsterType} instance.
     *
     * @param rs the current result-set row; must not be {@code null}
     * @return a populated {@link MonsterType}
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
