package repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.MonsterHighGroup;
import model.Schedule;
import model.Subject;
import util.DatabaseConnection;

/**
 * DAO for Schedule persistence using JDBC + SQLite.
 * Extends {@link GenericRepositoryBD} and provides CRUD operations
 * for the SCHEDULE table, plus a query method to filter by group.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class ScheduleDAO extends GenericRepositoryBD<Schedule> {

    /** Used internally to resolve the Subject foreign key. */
    private final SubjectDAO subjectDAO = new SubjectDAO();

    /** Used internally to resolve the MonsterHighGroup foreign key. */
    private final MonsterHighGroupDAO groupDAO = new MonsterHighGroupDAO();

    /**
     * Inserts a new Schedule record into the database.
     * The id field is ignored; SQLite assigns it via AUTOINCREMENT.
     *
     * @param s the Schedule to save
     */
    @Override
    public void save(Schedule s) {
        String sql = "INSERT INTO SCHEDULE(subjectId, groupId, dayOfWeek, " +
                     "startTime, endTime, classroom) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getSubject().getId());
            ps.setInt(2, s.getGroup().getId());
            ps.setString(3, s.getDayOfWeek());
            ps.setString(4, s.getStartTime());
            ps.setString(5, s.getEndTime());
            ps.setString(6, s.getClassroom());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.save] " + e.getMessage());
        }
    }

    /**
     * Updates an existing Schedule record in the database.
     *
     * @param s the Schedule with updated data
     */
    @Override
    public void update(Schedule s) {
        String sql = "UPDATE SCHEDULE SET subjectId=?, groupId=?, dayOfWeek=?, " +
                     "startTime=?, endTime=?, classroom=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getSubject().getId());
            ps.setInt(2, s.getGroup().getId());
            ps.setString(3, s.getDayOfWeek());
            ps.setString(4, s.getStartTime());
            ps.setString(5, s.getEndTime());
            ps.setString(6, s.getClassroom());
            ps.setInt(7, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.update] " + e.getMessage());
        }
    }

    /**
     * Deletes the Schedule with the given ID.
     *
     * @param id the ID of the schedule entry to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM SCHEDULE WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Retrieves a Schedule by its ID.
     *
     * @param id the ID to search for
     * @return the found Schedule, or {@code null} if not present
     */
    @Override
    public Schedule findById(int id) {
        String sql = "SELECT * FROM SCHEDULE WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all Schedule records from the database.
     *
     * @return list of all schedule entries
     */
    @Override
    public List<Schedule> findAll() {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all Schedule entries for a specific group.
     *
     * @param groupId the ID of the group to filter by
     * @return list of schedule entries for that group
     */
    public List<Schedule> findByGroup(int groupId) {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE WHERE groupId=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.findByGroup] " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves all Schedule entries for a specific subject.
     *
     * @param subjectId the ID of the subject to filter by
     * @return list of schedule entries for that subject
     */
    public List<Schedule> findBySubject(int subjectId) {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT * FROM SCHEDULE WHERE subjectId=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.findBySubject] " + e.getMessage());
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link Schedule} object.
     * Resolves Subject and MonsterHighGroup FKs via their respective DAOs.
     *
     * @param rs the current ResultSet row
     * @return populated Schedule instance
     * @throws SQLException if any column cannot be read
     */
    private Schedule mapRow(ResultSet rs) throws SQLException {
        Subject          subject = subjectDAO.findById(rs.getInt("subjectId"));
        MonsterHighGroup group   = groupDAO.findById(rs.getInt("groupId"));
        return new Schedule(
                rs.getInt("id"),
                subject,
                group,
                rs.getString("dayOfWeek"),
                rs.getString("startTime"),
                rs.getString("endTime"),
                rs.getString("classroom")
        );
    }
}