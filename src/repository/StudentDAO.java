package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.MonsterType;
import model.Student;
import util.DatabaseConnection;

/**
 * Data Access Object for persisting {@link Student} entities using JDBC + SQLite.
 * Extends {@link GenericRepositoryBD} with the {@link Student} type.
 *
 * @author Fatima
 * @version 1.1
 */
public class StudentDAO extends GenericRepositoryBD<Student> {

    /**
     * Used internally to resolve the MonsterType foreign key.
     * Accepted via constructor to allow mock injection in unit tests.
     */
    private final MonsterTypeDAO monsterTypeDAO;

    /**
     * Default constructor. Creates its own {@link MonsterTypeDAO} instance.
     */
    public StudentDAO() {
        this.monsterTypeDAO = new MonsterTypeDAO();
    }

    /**
     * Dependency-injection constructor.
     * Useful for unit tests where a mock {@link MonsterTypeDAO} is needed.
     *
     * @param monsterTypeDAO the MonsterTypeDAO instance to use
     */
    public StudentDAO(MonsterTypeDAO monsterTypeDAO) {
        this.monsterTypeDAO = monsterTypeDAO;
    }

    /**
     * Inserts a new student record into the database.
     *
     * @param s the student to save
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void save(Student s) {
        String sql = "INSERT INTO STUDENT(id, name, surname, birthDate, email, " +
                     "studentYear, groupName, monsterTypeId) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, s.getId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getSurname());
            // FIX: store null when birthDate is absent instead of LocalDate.now()
            ps.setString(4, s.getBirthDate() != null ? s.getBirthDate().toString() : null);
            ps.setString(5, s.getEmail());
            ps.setInt(6, s.getStudentYear());
            ps.setString(7, s.getGroupName());
            if (s.getMonsterType() != null) ps.setInt(8, s.getMonsterType().getId());
            else ps.setNull(8, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            // FIX: rethrow so the caller knows the operation failed
            throw new RuntimeException("[StudentDAO.save] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing student record in the database.
     *
     * @param s the student with updated values (must carry a valid id)
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void update(Student s) {
        String sql = "UPDATE STUDENT SET name=?, surname=?, birthDate=?, email=?, " +
                     "studentYear=?, groupName=?, monsterTypeId=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getSurname());
            ps.setString(3, s.getBirthDate() != null ? s.getBirthDate().toString() : null);
            ps.setString(4, s.getEmail());
            ps.setInt(5, s.getStudentYear());
            ps.setString(6, s.getGroupName());
            if (s.getMonsterType() != null) ps.setInt(7, s.getMonsterType().getId());
            else ps.setNull(7, Types.INTEGER);
            ps.setInt(8, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[StudentDAO.update] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the student with the given id from the database.
     *
     * @param id the identifier of the student to remove
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM STUDENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[StudentDAO.deleteById] Database error: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a student by its id.
     *
     * @param id the identifier to look up
     * @return the matching student, or {@code null} if none exists with that id
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public Student findById(int id) {
        String sql = "SELECT * FROM STUDENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("[StudentDAO.findById] Database error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns all students stored in the database.
     *
     * @return list of students (empty if none exist)
     * @throws RuntimeException if a database error occurs
     */
    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("[StudentDAO.findAll] Database error: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Maps the current row of a {@link ResultSet} into a {@link Student} object.
     * Resolves the MonsterType foreign key via {@link MonsterTypeDAO}.
     *
     * @param rs the current ResultSet row
     * @return the Student built from that row
     * @throws SQLException if any column cannot be read
     */
    private Student mapRow(ResultSet rs) throws SQLException {
        int mtId = rs.getInt("monsterTypeId");
        MonsterType mt = rs.wasNull() ? null : monsterTypeDAO.findById(mtId);
        String bd = rs.getString("birthDate");
        return new Student(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("surname"),
            // FIX: return null when birthDate is absent in the DB instead of LocalDate.now()
            bd != null ? LocalDate.parse(bd) : null,
            rs.getString("email"),
            rs.getInt("studentYear"),
            rs.getString("groupName"),
            mt
        );
    }
}