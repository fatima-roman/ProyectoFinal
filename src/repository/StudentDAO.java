package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import exceptions.DatabaseException;
import model.MonsterType;
import model.Student;
import util.DatabaseConnection;

/**
 * Data Access Object for {@link Student} entities.
 *
 * Handles all CRUD operations against the {@code STUDENT} table using
 * JDBC + SQLite. Foreign-key resolution (MonsterType) is performed after the main {@link ResultSet} is closed to avoid the
 * "stmt pointer is closed" error that SQLite raises when two queries
 * share the same connection concurrently.
 *
 * @author Fatima Roman
 * @version 2.0
 */
public class StudentDAO extends GenericRepositoryBD<Student> {

    /** DAO used to resolve the MonsterType foreign key. */
    private final MonsterTypeDAO monsterTypeDAO = new MonsterTypeDAO();

    /**
     * Inserts a new {@link Student} record into the database.
     *
     * @param student the student to persist; must not be {@code null}
     * @throws DatabaseException if a SQL error occurs during insertion
     */
    @Override
    public void save(Student student) {
        String sql = "INSERT INTO STUDENT(id, name, surname, birthDate, email, " +
                     "studentYear, groupName, monsterTypeId) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt   (1, student.getId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getSurname());
            ps.setString(4, student.getBirthDate() != null
                            ? student.getBirthDate().toString() : null);
            ps.setString(5, student.getEmail());
            ps.setInt   (6, student.getStudentYear());
            ps.setString(7, student.getGroupName());

            if (student.getMonsterType() != null) {
                ps.setInt(8, student.getMonsterType().getId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("[StudentDAO.save] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing {@link Student} record in the database.
     *
     * @param student the student with updated field values; must not be {@code null}
     * @throws DatabaseException if a SQL error occurs during the update
     */
    @Override
    public void update(Student student) {
        String sql = "UPDATE STUDENT SET name=?, surname=?, birthDate=?, email=?, " +
                     "studentYear=?, groupName=?, monsterTypeId=? WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getSurname());
            ps.setString(3, student.getBirthDate() != null
                            ? student.getBirthDate().toString() : null);
            ps.setString(4, student.getEmail());
            ps.setInt   (5, student.getStudentYear());
            ps.setString(6, student.getGroupName());

            if (student.getMonsterType() != null) {
                ps.setInt(7, student.getMonsterType().getId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            ps.setInt(8, student.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("[StudentDAO.update] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the {@link Student} with the given ID from the database.
     *
     * @param id the primary key of the student to remove
     * @throws DatabaseException if a SQL error occurs during deletion
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM STUDENT WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("[StudentDAO.deleteById] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a single {@link Student} by its primary key.
     *
     * The {@link MonsterType} foreign key is resolved in a separate query
     * after the main ResultSet is fully closed, preventing
     * concurrent-statement errors in SQLite.
     *
     * @param id the student ID to look up
     * @return the matching {@link Student}, or {@code null} if not found
     * @throws DatabaseException if a SQL error occurs during the query
     */
    @Override
    public Student findById(int id) {
        String sql = "SELECT * FROM STUDENT WHERE id=?";

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            int    studentId   = -1;
            String name        = null;
            String surname     = null;
            String birthDate   = null;
            String email       = null;
            int    year        = 0;
            String groupName   = null;
            int    mtId        = 0;
            boolean mtNull     = true;

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    studentId = rs.getInt("id");
                    name      = rs.getString("name");
                    surname   = rs.getString("surname");
                    birthDate = rs.getString("birthDate");
                    email     = rs.getString("email");
                    year      = rs.getInt("studentYear");
                    groupName = rs.getString("groupName");
                    mtId      = rs.getInt("monsterTypeId");
                    mtNull    = rs.wasNull();
                } else {
                    return null; 
                }
            }
            MonsterType mt = mtNull ? null : monsterTypeDAO.findById(mtId);

            return new Student(
                studentId, name, surname,
                birthDate != null ? LocalDate.parse(birthDate) : null,
                email, year, groupName, mt
            );

        } catch (SQLException e) {
            throw new DatabaseException("[StudentDAO.findById] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all {@link Student} records from the database.
     *
     * <p>Uses the same two-phase approach as {@link #findById(int)}: raw data
     * is collected in a temporary list while the ResultSet is open; then
     * {@link MonsterType} objects are resolved after it is closed.</p>
     *
     * @return a (possibly empty) list of all students
     * @throws DatabaseException if a SQL error occurs during the query
     */
    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM STUDENT";

        record RawRow(int id, String name, String surname, String birthDate,
                      String email, int year, String groupName,
                      int mtId, boolean mtNull) {}

        List<RawRow> rows = new ArrayList<>();

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int    mtId   = rs.getInt("monsterTypeId");
                boolean mtNull = rs.wasNull();
                rows.add(new RawRow(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("surname"),
                    rs.getString("birthDate"),
                    rs.getString("email"),
                    rs.getInt("studentYear"),
                    rs.getString("groupName"),
                    mtId, mtNull
                ));
            }

        } catch (SQLException e) {
            throw new DatabaseException("[StudentDAO.findAll] Database error: " + e.getMessage(), e);
        }

        List<Student> students = new ArrayList<>();
        for (RawRow r : rows) {
            MonsterType mt = r.mtNull() ? null : monsterTypeDAO.findById(r.mtId());
            students.add(new Student(
                r.id(), r.name(), r.surname(),
                r.birthDate() != null ? LocalDate.parse(r.birthDate()) : null,
                r.email(), r.year(), r.groupName(), mt
            ));
        }
        return students;
    }
}