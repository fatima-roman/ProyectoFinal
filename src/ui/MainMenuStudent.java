package ui;

import exceptions.StudentNotFoundException;
import model.MonsterType;
import model.Student;
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
 * @version 2.1
 */
public class MainMenuStudent {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for student operations. */
    private static final StudentService studentService = new StudentService();

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
        if (students.isEmpty()) { System.out.println("No students registered."); return; }
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
        LocalDate birthDate = readDate();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Year (1 or 2): ");
        int year = readYear();
        System.out.print("Group name (e.g. 1A): ");
        String groupName = sc.nextLine().trim();
        System.out.print("Monster Type ID (1=Vampire 2=Werewolf 3=Zombie 4=Witch 5=Mummy): ");
        int mtId = MainMenu.readInt();
        MonsterType mt = new MonsterType(mtId, null, null, null, 0);
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
        LocalDate birthDate = rawDate.isEmpty() ? existing.getBirthDate() : parseDate(rawDate, existing.getBirthDate());
        System.out.print("New email [" + existing.getEmail() + "]: ");
        String email = readOptional(existing.getEmail());
        System.out.print("New year [" + existing.getStudentYear() + "]: ");
        String rawYear = sc.nextLine().trim();
        int year = rawYear.isEmpty() ? existing.getStudentYear() : Integer.parseInt(rawYear);
        System.out.print("New group [" + existing.getGroupName() + "]: ");
        String groupName = readOptional(existing.getGroupName());
        int currentMtId = existing.getMonsterType() != null ? existing.getMonsterType().getId() : 0;
        System.out.print("New Monster Type ID [" + currentMtId + "]: ");
        String rawMtId = sc.nextLine().trim();
        int mtId = rawMtId.isEmpty() ? currentMtId : Integer.parseInt(rawMtId);
        MonsterType mt = new MonsterType(mtId, null, null, null, 0);
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
            System.out.print("Are you sure you want to delete: " + s.getName() + " " + s.getSurname() + "? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Deletion cancelled."); return; }
            studentService.deleteStudent(id);
            System.out.println("Student deleted successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Reads a valid ISO date, re-prompting on invalid input.
     *
     * @return parsed {@link LocalDate}
     */
    private static LocalDate readDate() {
        while (true) {
            try { return LocalDate.parse(sc.nextLine().trim()); }
            catch (DateTimeParseException e) { System.out.print("Invalid date, use YYYY-MM-DD: "); }
        }
    }

    /**
     * Parses a date string; returns {@code defaultValue} if parsing fails.
     *
     * @param raw          the raw date string
     * @param defaultValue fallback date
     * @return parsed date or {@code defaultValue}
     */
    private static LocalDate parseDate(String raw, LocalDate defaultValue) {
        try { return LocalDate.parse(raw); }
        catch (DateTimeParseException e) { System.out.println("Invalid date, keeping original."); return defaultValue; }
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
                System.out.print("Year must be 1 or 2: ");
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
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
