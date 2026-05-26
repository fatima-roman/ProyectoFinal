package ui;

import exceptions.StudentNotFoundException;
import model.MonsterType;
import model.Student;
import service.MonsterTypeService;
import service.StudentService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for student management.
 * Provides full CRUD operations on {@link Student} entities via {@link StudentService}.
 *
 * @author Fatima Roman
 * @version 2.2
 */
public class MainMenuStudent {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for student operations. */
    private static final StudentService studentService = new StudentService();

    /** Service layer for monster type validation. */
    private static final MonsterTypeService monsterTypeService = new MonsterTypeService();

    /**
     * Starts the student submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> listAllStudents();
                case 2 -> findStudentById();
                case 3 -> addStudent();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints the student submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== STUDENT MANAGEMENT =====");
        System.out.println("1. List all students");
        System.out.println("2. Find student by ID");
        System.out.println("3. Add new student");
        System.out.println("4. Update student");
        System.out.println("5. Delete student");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Retrieves and prints all students.
     */
    private static void listAllStudents() {
        List<Student> students = studentService.findAll();
        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }
        System.out.println("\n--- Student List (" + students.size() + " total) ---");
        students.forEach(System.out::println);
    }

    /**
     * Prompts for an ID and prints the matching student.
     */
    private static void findStudentById() {
        System.out.print("Enter student ID: ");
        int id = MainMenu.readInt();
        try {
            System.out.println("\n" + studentService.findById(id));
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for all student fields and saves a new student.
     */
    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Surname: ");
        String surname = sc.nextLine().trim();

        System.out.print("Birth date (YYYY-MM-DD): ");
        LocalDate birthDate = readDateInPast();  

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Year (1 or 2): ");
        int year = readYear();

        System.out.print("Group name (e.g. 1A): ");
        String groupName = sc.nextLine().trim();

        MonsterType mt = readMonsterType();       
        if (mt == null) return;                   

        // ID 0 = AUTOINCREMENT, DB assigns the real id
        Student newStudent = new Student(0, name, surname, birthDate, email, year, groupName, mt);
        try {
            studentService.save(newStudent);
            System.out.println("Student added successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for an ID, loads the existing record, then allows updating each field.
     */
    private static void updateStudent() {
        System.out.print("\nEnter the ID of the student to update: ");
        int id = MainMenu.readInt();
        Student existing;
        try {
            existing = studentService.findById(id);
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.println("Editing: " + existing);
        System.out.println("(Press Enter to keep current value)\n");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = readOptional(existing.getName());

        System.out.print("New surname [" + existing.getSurname() + "]: ");
        String surname = readOptional(existing.getSurname());

        System.out.print("New birth date [" + existing.getBirthDate() + "] (YYYY-MM-DD): ");
        String rawDate = sc.nextLine().trim();
        LocalDate birthDate = rawDate.isEmpty()
                ? existing.getBirthDate()
                : parseDateInPast(rawDate, existing.getBirthDate()); // FIX: valida en pasado

        System.out.print("New email [" + existing.getEmail() + "]: ");
        String email = readOptional(existing.getEmail());

        System.out.print("New year [" + existing.getStudentYear() + "]: ");
        String rawYear = sc.nextLine().trim();
        int year = rawYear.isEmpty() ? existing.getStudentYear() : parseYear(rawYear, existing.getStudentYear());

        System.out.print("New group [" + existing.getGroupName() + "]: ");
        String groupName = readOptional(existing.getGroupName());

        int currentMtId = existing.getMonsterType() != null ? existing.getMonsterType().getId() : 0;
        System.out.print("New Monster Type ID [" + currentMtId + "] (Enter to keep): ");
        String rawMtId = sc.nextLine().trim();

        MonsterType mt;
        if (rawMtId.isEmpty()) {
            mt = existing.getMonsterType();
        } else {
            try {
                mt = monsterTypeService.findById(Integer.parseInt(rawMtId)); // FIX: valida en BD
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + " – keeping original Monster Type.");
                mt = existing.getMonsterType();
            }
        }

        Student updated = new Student(id, name, surname, birthDate, email, year, groupName, mt);
        try {
            studentService.update(updated);
            System.out.println("Student updated successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for an ID and deletes the student after confirmation.
     */
    private static void deleteStudent() {
        System.out.print("\nEnter student ID to delete: ");
        int id = MainMenu.readInt();
        try {
            Student s = studentService.findById(id);
            System.out.print("Are you sure you want to delete: "
                    + s.getName() + " " + s.getSurname() + "? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }
            studentService.deleteStudent(id);
            System.out.println("Student deleted successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Reads a valid ISO date in the past, re-prompting until the date
     * is both parseable and strictly before today.
     *
     * @return parsed {@link LocalDate} that is before {@link LocalDate#now()}
     */
    private static LocalDate readDateInPast() {
        while (true) {
            try {
                LocalDate date = LocalDate.parse(sc.nextLine().trim());
                if (date.isBefore(LocalDate.now())) {
                    return date;
                }
                System.out.print("La fecha debe ser anterior a hoy. Introduce otra (YYYY-MM-DD): ");
            } catch (DateTimeParseException e) {
                System.out.print("Formato inválido, usa YYYY-MM-DD: ");
            }
        }
    }

    /**
     * Parses a date string and validates it is in the past.
     * Returns {@code defaultValue} if parsing fails or the date is not in the past.
     *
     * @param raw          the raw date string
     * @param defaultValue fallback date
     * @return parsed date (if valid and in the past) or {@code defaultValue}
     */
    private static LocalDate parseDateInPast(String raw, LocalDate defaultValue) {
        try {
            LocalDate date = LocalDate.parse(raw);
            if (date.isBefore(LocalDate.now())) {
                return date;
            }
            System.out.println("La fecha debe ser anterior a hoy. Se mantiene la original.");
            return defaultValue;
        } catch (DateTimeParseException e) {
            System.out.println("Formato inválido. Se mantiene la fecha original.");
            return defaultValue;
        }
    }

    /**
     * Reads a valid Monster Type ID and fetches it from the database,
     * re-prompting until the ID actually exists.
     *
     * @return the {@link MonsterType} found in the database, or {@code null} on unexpected error
     */
    private static MonsterType readMonsterType() {
        System.out.print("Monster Type ID (1=Vampire 2=Werewolf 3=Zombie 4=Witch 5=Mummy): ");
        while (true) {
            try {
                int mtId = Integer.parseInt(sc.nextLine().trim());
                MonsterType mt = monsterTypeService.findById(mtId); // lanza excepción si no existe
                return mt;
            } catch (NumberFormatException e) {
                System.out.print("Introduce un número válido: ");
            } catch (Exception e) {
                System.out.print("Error: " + e.getMessage() + ". Introduce otro ID: ");
            }
        }
    }

    /**
     * Reads a year value (1 or 2), re-prompting until valid.
     *
     * @return valid year value
     */
    private static int readYear() {
        while (true) {
            try {
                int y = Integer.parseInt(sc.nextLine().trim());
                if (y == 1 || y == 2) return y;
                System.out.print("El año debe ser 1 o 2: ");
            } catch (NumberFormatException e) {
                System.out.print("Introduce un número válido: ");
            }
        }
    }

    /**
     * Parses a year string (1 or 2). Returns {@code defaultValue} if invalid.
     *
     * @param raw          raw input string
     * @param defaultValue fallback year
     * @return parsed year if valid, otherwise {@code defaultValue}
     */
    private static int parseYear(String raw, int defaultValue) {
        try {
            int y = Integer.parseInt(raw);
            if (y == 1 || y == 2) return y;
            System.out.println("El año debe ser 1 o 2. Se mantiene el original.");
            return defaultValue;
        } catch (NumberFormatException e) {
            System.out.println("Número inválido. Se mantiene el año original.");
            return defaultValue;
        }
    }

    /**
     * Returns {@code defaultValue} if the user presses Enter without input.
     *
     * @param defaultValue fallback value
     * @return user input or {@code defaultValue}
     */
    private static String readOptional(String defaultValue) {
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}