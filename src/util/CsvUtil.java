package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

import model.MonsterType;
import model.Student;
import model.Subject;
import model.Teacher;
import repository.MonsterTypeDAO;
import repository.StudentDAO;
import repository.SubjectDAO;
import repository.TeacherDAO;

/**
 * Utility class for importing data from CSV files into the database.
 *
 * @author Fatima
 * @version 1.0
 */
public class CsvUtil {

    /**
     * Imports MonsterType records from a CSV file.
     * @param path path to the CSV file
     * @param dao  MonsterTypeDAO to persist the records
     */
    public static void importMonsterTypes(String path, MonsterTypeDAO dao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                MonsterType mt = new MonsterType(
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(), p[2].trim(), p[3].trim(),
                    Integer.parseInt(p[4].trim())
                );
                dao.save(mt);
            }
            System.out.println("MonsterTypes imported from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil: " + e.getMessage());
        }
    }

    /**
     * Imports Teacher records from a CSV file.
     * @param path path to the CSV file
     * @param dao  TeacherDAO to persist the records
     */
    public static void importTeachers(String path, TeacherDAO dao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                Teacher t = new Teacher(
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(), p[2].trim(),
                    LocalDate.parse(p[3].trim()),
                    p[4].trim(), p[5].trim()
                );
                dao.save(t);
            }
            System.out.println("Teachers imported from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil: " + e.getMessage());
        }
    }

    /**
     * Imports Student records from a CSV file.
     * @param path    path to the CSV file
     * @param dao     StudentDAO to persist the records
     * @param mtDao   MonsterTypeDAO to resolve the FK
     */
    public static void importStudents(String path, StudentDAO dao, MonsterTypeDAO mtDao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                MonsterType mt = mtDao.findById(Integer.parseInt(p[7].trim()));
                Student s = new Student(
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(), p[2].trim(),
                    LocalDate.parse(p[3].trim()),
                    p[4].trim(),
                    Integer.parseInt(p[5].trim()),
                    p[6].trim(), mt
                );
                dao.save(s);
            }
            System.out.println("Students imported from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil: " + e.getMessage());
        }
    }

    /**
     * Imports Subject records from a CSV file.
     * @param path      path to the CSV file
     * @param dao       SubjectDAO to persist the records
     * @param teacherDao TeacherDAO to resolve the FK
     */
    public static void importSubjects(String path, SubjectDAO dao, TeacherDAO teacherDao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                int teacherId = Integer.parseInt(p[3].trim());
                Teacher t = teacherDao.findById(teacherId);
                Subject s = new Subject(
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(),
                    Integer.parseInt(p[2].trim()),
                    t
                );
                dao.save(s);
            }
            System.out.println("Subjects imported from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil: " + e.getMessage());
        }
    }
}