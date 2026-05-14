package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.MonsterType;
import model.Student;
import util.DatabaseConnection;

/**
 * DAO for Student persistence using JDBC + SQLite.
 *
 * @author Fatima
 * @version 1.0
 */
public class StudentDAO extends GenericRepositoryBD<Student> {

    /** Used internally to resolve the MonsterType foreign key. */
    private final MonsterTypeDAO monsterTypeDAO = new MonsterTypeDAO();

    /**
     * Inserts a new Student record into the database.
     *
     * @param s the Student to save
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
            ps.setString(4, s.getBirthDate() != null ? s.getBirthDate().toString() : null);
            ps.setString(5, s.getEmail());
            ps.setInt(6, s.getStudentYear());
            ps.setString(7, s.getGroupName());
            if (s.getMonsterType() != null) ps.setInt(8, s.getMonsterType().getId());
            else ps.setNull(8, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[StudentDAO.save] " + e.getMessage());
        }
    }

    /**
     * Updates an existing Student record in the database.
     *
     * @param s the Student with updated data
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
            System.err.println("[StudentDAO.update] " + e.getMessage());
        }
    }

    /**
     * Deletes the Student with the given ID.
     *
     * @param id the ID of the Student to delete
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM STUDENT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[StudentDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Retrieves a Student by its ID.
     *
     * @param id the ID to search for
     * @return the found Student, or {@code null} if not present
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
            System.err.println("[StudentDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all Student records from the database.
     *
     * @return list of all students
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
            System.err.println("[StudentDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    /**
     * Maps a {@link ResultSet} row to a {@link Student} object.
     * Resolves the MonsterType FK via {@link MonsterTypeDAO}.
     *
     * @param rs the current ResultSet row
     * @return populated Student instance
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
            bd != null ? LocalDate.parse(bd) : LocalDate.now(),
            rs.getString("email"),
            rs.getInt("studentYear"),
            rs.getString("groupName"),
            mt
        );
    }
}
