package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.MonsterHighGroup;
import model.Teacher;
import util.DatabaseConnection;

/**
 * DAO for MonsterHighGroup persistence using JDBC + SQLite.
 *
 * @author Fatima R
 * @version 1.0
 */
public class MonsterHighGroupDAO extends GenericRepositoryBD<MonsterHighGroup> {

    /** Used internally to resolve the Teacher (tutor) foreign key. */
    private final TeacherDAO teacherDAO = new TeacherDAO();

    /**
     * Inserts a new MonsterHighGroup record into the database.
     *
     * @param g the MonsterHighGroup to save
     */
    @Override
    public void save(MonsterHighGroup g) {
    	String sql = "INSERT INTO MONSTER_GROUP(name, tutorId) VALUES(?, ?)";
    	try (Connection c = DatabaseConnection.getInstance().getConnection();
    		     PreparedStatement ps = c.prepareStatement(sql)) {
    		    ps.setString(1, g.getName());
    		    if (g.getTutor() != null) ps.setInt(2, g.getTutor().getId());
    		    else ps.setNull(2, Types.INTEGER);
    		    ps.executeUpdate();
    		} catch (SQLException e) {
            System.err.println("[MonsterHighGroupDAO.save] " + e.getMessage());
        }
    }

    /**
     * Updates an existing MonsterHighGroup record in the database.
     *
     * @param g the MonsterHighGroup with updated data
     */
    @Override
    public void update(MonsterHighGroup g) {
        String sql = "UPDATE MONSTER_GROUP SET name=?, tutorId=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, g.getName());
            if (g.getTutor() != null) ps.setInt(2, g.getTutor().getId());
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MonsterHighGroupDAO.update] " + e.getMessage());
        }
    }

    /**
     * Deletes the MonsterHighGroup with the given ID.
     *
     * @param id the ID of the group to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM MONSTER_GROUP WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MonsterHighGroupDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Retrieves a MonsterHighGroup by its ID.
     *
     * @param id the ID to search for
     * @return the found group, or {@code null} if not present
     */
    @Override
    public MonsterHighGroup findById(int id) {
        String sql = "SELECT * FROM MONSTER_GROUP WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MonsterHighGroupDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all MonsterHighGroup records from the database.
     *
     * @return list of all groups
     */
    @Override
    public List<MonsterHighGroup> findAll() {
        List<MonsterHighGroup> list = new ArrayList<>();
        String sql = "SELECT id, name, tutorId FROM MONSTER_GROUP";
        List<int[]> rows = new ArrayList<>();
        List<String> names = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new int[]{ rs.getInt("id"), rs.getInt("tutorId"),
                                    rs.wasNull() ? 1 : 0 });
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("[MonsterHighGroupDAO.findAll] " + e.getMessage());
            return list;
        }
        for (int i = 0; i < rows.size(); i++) {
            int     id      = rows.get(i)[0];
            int     tutorId = rows.get(i)[1];
            boolean noTutor = rows.get(i)[2] == 1;
            Teacher tutor   = noTutor ? null : teacherDAO.findById(tutorId);
            list.add(new MonsterHighGroup(id, names.get(i), tutor));
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link MonsterHighGroup} object.
     * Resolves the Teacher (tutor) FK via {@link TeacherDAO}.
     *
     * @param rs the current ResultSet row
     * @return populated MonsterHighGroup instance
     * @throws SQLException if any column cannot be read
     */
    private MonsterHighGroup mapRow(ResultSet rs) throws SQLException {
        int    id      = rs.getInt("id");
        String name    = rs.getString("name");
        int    tutorId = rs.getInt("tutorId");
        boolean noTutor = rs.wasNull();
        Teacher tutor   = noTutor ? null : teacherDAO.findById(tutorId);
        return new MonsterHighGroup(id, name, tutor);
    }
}
