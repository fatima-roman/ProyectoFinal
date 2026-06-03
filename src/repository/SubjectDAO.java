package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import exceptions.DatabaseException;
import model.Subject;
import model.Teacher;
import util.DatabaseConnection;

/**
 * DAO for Subject persistence using JDBC + SQLite.
 *
 * @author Fatima
 * @version 1.1
 */
public class SubjectDAO extends GenericRepositoryBD<Subject> {

    /**
     * Inserts a new Subject record into the database.
     *
     * @param s the Subject to save
     */
    @Override
    public void save(Subject s) {
        String sql = "INSERT INTO SUBJECT(name, course, teacherId) VALUES(?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setInt(2, s.getCourse());

            if (s.getTeacher() != null) {
                ps.setInt(3, s.getTeacher().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("[SubjectDAO.save] " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing Subject record.
     *
     * @param s the Subject with updated data
     */
    @Override
    public void update(Subject s) {
        String sql = "UPDATE SUBJECT SET name=?, course=?, teacherId=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setInt(2, s.getCourse());

            if (s.getTeacher() != null) {
                ps.setInt(3, s.getTeacher().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setInt(4, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("[SubjectDAO.save] " + e.getMessage(), e);
        }
    }

    /**
     * Deletes the Subject with the given ID.
     *
     * @param id the Subject ID
     */
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM SUBJECT WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[SubjectDAO.deleteById] " + e.getMessage());
        }
    }

    /**
     * Finds a Subject by ID.
     *
     * @param id the Subject ID
     * @return the subject found or null
     */
    @Override
    public Subject findById(int id) {
        String sql = """
                SELECT s.id, s.name, s.course,
                       t.id AS teacher_id,
                       t.name AS teacher_name,
                       t.surname AS teacher_surname,
                       t.birthDate AS teacher_birthDate,
                       t.email AS teacher_email,
                       t.specialty AS teacher_specialty
                FROM SUBJECT s
                LEFT JOIN TEACHER t ON s.teacherId = t.id
                WHERE s.id = ?
                """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowWithTeacher(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("[SubjectDAO.save] " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Retrieves all Subject records from the database.
     *
     * @return list of all subjects
     */
    @Override
    public List<Subject> findAll() {
        List<Subject> list = new ArrayList<>();

        String sql = """
                SELECT s.id, s.name, s.course,
                       t.id AS teacher_id,
                       t.name AS teacher_name,
                       t.surname AS teacher_surname,
                       t.birthDate AS teacher_birthDate,
                       t.email AS teacher_email,
                       t.specialty AS teacher_specialty
                FROM SUBJECT s
                LEFT JOIN TEACHER t ON s.teacherId = t.id
                ORDER BY s.id
                """;

        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowWithTeacher(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("[SubjectDAO.save] " + e.getMessage(), e);
        }

        return list;
    }

    /**
     * Maps a ResultSet row to a Subject object with teacher data.
     *
     * @param rs current row
     * @return mapped Subject
     * @throws SQLException if any DB access error occurs
     */
    private Subject mapRowWithTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = null;

        int teacherId = rs.getInt("teacher_id");
        if (!rs.wasNull()) {
        	String rawDate = rs.getString("teacher_birthDate");
        	LocalDate birthDate = null;

        	if (rawDate != null && !rawDate.isBlank()) {
        	    try {
        	        birthDate = LocalDate.parse(rawDate);
        	    } catch (Exception e) {
        	        System.err.println("[SubjectDAO.mapRowWithTeacher] Invalid birthDate format: " + rawDate);
        	    }
        	}

            teacher = new Teacher(
                teacherId,
                rs.getString("teacher_name"),
                rs.getString("teacher_surname"),
                birthDate,
                rs.getString("teacher_email"),
                rs.getString("teacher_specialty")
            );
        }

        return new Subject(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("course"),
            teacher
        );
    }
}