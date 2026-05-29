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
        return querySchedules("SELECT * FROM SCHEDULE", -1, -1);
    }

    /**
     * Retrieves all Schedule entries for a specific group.
     *
     * @param groupId the ID of the group to filter by
     * @return list of schedule entries for that group
     */
    public List<Schedule> findByGroup(int groupId) {
        return querySchedules("SELECT * FROM SCHEDULE WHERE groupId=?", groupId, -1);
    }

    /**
     * Retrieves all Schedule entries for a specific subject.
     *
     * @param subjectId the ID of the subject to filter by
     * @return list of schedule entries for that subject
     */
    public List<Schedule> findBySubject(int subjectId) {
        return querySchedules("SELECT * FROM SCHEDULE WHERE subjectId=?", -1, subjectId);
    }

    /**
     * Internal helper that executes a parameterised SCHEDULE query and maps
     * the results to {@link Schedule} objects in two separate phases to avoid
     * the "stmt pointer is closed" error that occurs when a nested DAO call
     * (to resolve a foreign key) opens a new query while the outer
     * {@link ResultSet} is still open on the same SQLite connection.
     *
     * <p><b>Phase 1</b> — reads all raw column values into an intermediate
     * {@code Row} record list while the {@link ResultSet} is open, then closes
     * the statement and connection via try-with-resources.</p>
     *
     * <p><b>Phase 2</b> — once the {@link ResultSet} is fully closed, resolves
     * the {@code subjectId} and {@code groupId} foreign keys through
     * {@link SubjectDAO} and {@link MonsterHighGroupDAO} respectively, and
     * builds the final {@link Schedule} objects.</p>
     *
     * <p>Rows whose subject or group cannot be resolved (orphaned FK) are
     * silently skipped.</p>
     *
     * <p>Exactly one of {@code groupId} or {@code subjectId} should be a valid
     * positive ID; pass {@code -1} for the parameter that does not apply.
     * If both are {@code -1} the SQL must not contain any {@code ?} placeholder
     * (e.g. {@code SELECT * FROM SCHEDULE}).</p>
     *
     * @param sql        the parameterised SQL query to execute
     * @param groupId    the group ID to bind to the first {@code ?} placeholder,
     *                   or {@code -1} if filtering by group is not required
     * @param subjectId  the subject ID to bind to the first {@code ?} placeholder,
     *                   or {@code -1} if filtering by subject is not required
     * @return a list of fully resolved {@link Schedule} objects; never {@code null},
     *         empty if no rows match or an error occurs
     */
    private List<Schedule> querySchedules(String sql, int groupId, int subjectId) {
        List<Schedule> list = new ArrayList<>();
        record Row(int id, int sid, int gid, String day,
                   String start, String end, String room) {}
        List<Row> rows = new ArrayList<>();

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (groupId   != -1) ps.setInt(1, groupId);
            if (subjectId != -1) ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Row(
                            rs.getInt("id"),
                            rs.getInt("subjectId"),
                            rs.getInt("groupId"),
                            rs.getString("dayOfWeek"),
                            rs.getString("startTime"),
                            rs.getString("endTime"),
                            rs.getString("classroom")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ScheduleDAO.findAll] " + e.getMessage());
            return list;
        }

        for (Row r : rows) {
            Subject          subject = subjectDAO.findById(r.sid());
            MonsterHighGroup group   = groupDAO.findById(r.gid());
            if (subject == null || group == null) continue;
            list.add(new Schedule(r.id(), subject, group,
                                  r.day(), r.start(), r.end(), r.room()));
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
        int    id  = rs.getInt("id");
        int    sid = rs.getInt("subjectId");
        int    gid = rs.getInt("groupId");
        String day   = rs.getString("dayOfWeek");
        String start = rs.getString("startTime");
        String end   = rs.getString("endTime");
        String room  = rs.getString("classroom");
        Subject          subject = subjectDAO.findById(sid);
        MonsterHighGroup group   = groupDAO.findById(gid);
        return new Schedule(id, subject, group, day, start, end, room);
    }
}