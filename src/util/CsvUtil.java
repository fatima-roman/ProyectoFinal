package util;

import model.MonsterType;
import model.Student;
import model.Subject;
import model.Teacher;
import repository.MonsterTypeDAO;
import repository.StudentDAO;
import repository.SubjectDAO;
import repository.TeacherDAO;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Utility class for importing data from CSV files and exporting data to CSV files.
 * <p>
 * Default paths follow the project layout:
 * <pre>resources/initial_data/students.csv</pre>
 * <pre>resources/initial_data/teachers.csv</pre>
 * <pre>resources/initial_data/subjects.csv</pre>
 * <pre>resources/initial_data/monster_types.csv</pre>
 * <p>
 * All methods are static; this class is not meant to be instantiated.
 *
 * @author Fatima Roman
 * @version 2.0
 */
public class CsvUtil {

    //Default paths

    /** Default CSV path for student data. */
    public static final String STUDENTS_PATH  = "src/resources/initial/students.csv";

    /** Default CSV path for teacher data. */
    public static final String TEACHERS_PATH  = "src/resources/initial/teachers.csv";

    /** Default CSV path for subject data. */
    public static final String SUBJECTS_PATH  = "src/resources/initial/subjects.csv";

    /** Default CSV path for monster type data. */
    public static final String MONSTER_TYPES_PATH = "src/resources/initial/monster_types.csv";

    /** Export output path for students. */
    public static final String EXPORT_STUDENTS_PATH  = "resources/export/students_export.csv";

    /** Export output path for teachers. */
    public static final String EXPORT_TEACHERS_PATH  = "resources/export/teachers_export.csv";

    /** Export output path for subjects. */
    public static final String EXPORT_SUBJECTS_PATH  = "resources/export/subjects_export.csv";

    //DAO instances (package-private, reused by no-arg methods)

    private static final MonsterTypeDAO mtDao      = new MonsterTypeDAO();
    private static final TeacherDAO     teacherDao  = new TeacherDAO();
    private static final StudentDAO     studentDao  = new StudentDAO();
    private static final SubjectDAO     subjectDao  = new SubjectDAO();

    /** Private constructor — utility class, never instantiated. */
    private CsvUtil() {}

    /**
     * Imports {@link Student} records from the default CSV path.
     * Relies on the MONSTER_TYPE table being populated beforehand to resolve the FK.
     */
    public static void importStudents() {
        importStudents(STUDENTS_PATH, studentDao, mtDao);
    }

    /**
     * Imports {@link Teacher} records from the default CSV path.
     */
    public static void importTeachers() {
        importTeachers(TEACHERS_PATH, teacherDao);
    }

    /**
     * Imports {@link Subject} records from the default CSV path.
     * Relies on the TEACHER table being populated beforehand to resolve the FK.
     */
    public static void importSubjects() {
        importSubjects(SUBJECTS_PATH, subjectDao, teacherDao);
    }

    /**
     * Imports {@link MonsterType} records from the default CSV path.
     */
    public static void importMonsterTypes() {
        importMonsterTypes(MONSTER_TYPES_PATH, mtDao);
    }

    /**
     * Exports all {@link Student} records to the default export path.
     */
    public static void exportStudents() {
        List<Student> list = studentDao.findAll();
        exportToFile(EXPORT_STUDENTS_PATH,
                "id,name,surname,birthDate,email,year,group,monsterTypeId",
                list.stream().map(Student::toCsv).toList());
    }

    /**
     * Exports all {@link Teacher} records to the default export path.
     */
    public static void exportTeachers() {
        List<Teacher> list = teacherDao.findAll();
        exportToFile(EXPORT_TEACHERS_PATH,
                "id,name,surname,birthDate,email,specialty",
                list.stream().map(Teacher::toCsv).toList());
    }

    /**
     * Exports all {@link Subject} records to the default export path.
     */
    public static void exportSubjects() {
        List<Subject> list = subjectDao.findAll();
        exportToFile(EXPORT_SUBJECTS_PATH,
                "id,name,course,teacherId",
                list.stream().map(Subject::toCsv).toList());
    }

    /**
     * Imports {@link MonsterType} records from the specified CSV file.
     * Expected CSV format (with header): {@code id,name,description,weakness,terrorLevel}
     * <p>The {@code id} column is read but ignored; the database assigns it via AUTOINCREMENT.</p>
     *
     * @param path path to the source CSV file
     * @param dao  {@link MonsterTypeDAO} used to persist each record
     */
    public static void importMonsterTypes(String path, MonsterTypeDAO dao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                MonsterType mt = new MonsterType(
                        0,
                        p[1].trim(),
                        p[2].trim(),
                        p[3].trim(),
                        Integer.parseInt(p[4].trim())
                );
                dao.save(mt);
                count++;
            }
            System.out.println("MonsterTypes imported: " + count + " records from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil.importMonsterTypes: " + e.getMessage());
        }
    }

    /**
     * Imports {@link Teacher} records from the specified CSV file.
     * Expected CSV format (with header): {@code id,name,surname,birthDate,email,specialty}
     * <p>The {@code id} column is read but ignored; the database assigns it via AUTOINCREMENT.</p>
     *
     * @param path path to the source CSV file
     * @param dao  {@link TeacherDAO} used to persist each record
     */
    public static void importTeachers(String path, TeacherDAO dao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                Teacher t = new Teacher(
                        0,
                        p[1].trim(),
                        p[2].trim(),
                        LocalDate.parse(p[3].trim()),
                        p[4].trim(),
                        p[5].trim()
                );
                dao.save(t);
                count++;
            }
            System.out.println("Teachers imported: " + count + " records from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil.importTeachers: " + e.getMessage());
        }
    }

    /**
     * Imports {@link Student} records from the specified CSV file.
     * Expected CSV format (with header):
     * {@code id,name,surname,birthDate,email,year,group,monsterTypeId}
     * <p>The {@code id} column is read but ignored; the database assigns it via AUTOINCREMENT.</p>
     *
     * @param path  path to the source CSV file
     * @param dao   {@link StudentDAO} used to persist each record
     * @param mtDao {@link MonsterTypeDAO} used to resolve the monster type foreign key
     */
    public static void importStudents(String path, StudentDAO dao, MonsterTypeDAO mtDao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                MonsterType mt = mtDao.findById(Integer.parseInt(p[7].trim()));
                Student s = new Student(
                        0,
                        p[1].trim(),
                        p[2].trim(),
                        LocalDate.parse(p[3].trim()),
                        p[4].trim(),
                        Integer.parseInt(p[5].trim()),
                        p[6].trim(),
                        mt
                );
                dao.save(s);
                count++;
            }
            System.out.println("Students imported: " + count + " records from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil.importStudents: " + e.getMessage());
        }
    }

    /**
     * Imports {@link Subject} records from the specified CSV file.
     * Expected CSV format (with header): {@code id,name,course,teacherId}
     * <p>The {@code id} column is read but ignored; the database assigns it via AUTOINCREMENT.</p>
     *
     * @param path       path to the source CSV file
     * @param dao        {@link SubjectDAO} used to persist each record
     * @param teacherDao {@link TeacherDAO} used to resolve the teacher foreign key
     */
    public static void importSubjects(String path, SubjectDAO dao, TeacherDAO teacherDao) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                int teacherId = Integer.parseInt(p[3].trim());
                Teacher t = teacherId > 0 ? teacherDao.findById(teacherId) : null;
                Subject s = new Subject(
                        0,
                        p[1].trim(),
                        Integer.parseInt(p[2].trim()),
                        t
                );
                dao.save(s);
                count++;
            }
            System.out.println("Subjects imported: " + count + " records from " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil.importSubjects: " + e.getMessage());
        }
    }


    /**
     * Writes a list of CSV lines to the specified file, creating parent directories
     * and writing the header row first.
     *
     * @param path   destination file path
     * @param header CSV header line
     * @param lines  data lines to write
     */
    private static void exportToFile(String path, String header, List<String> lines) {
        File file = new File(path);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println(header);
            lines.forEach(pw::println);
            System.out.println("Exported " + lines.size() + " records to " + path);
        } catch (IOException e) {
            System.err.println("CsvUtil.exportToFile: " + e.getMessage());
        }
    }
}
