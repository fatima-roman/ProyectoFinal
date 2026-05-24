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
 * Application entry point and main console menu for Monster High Institute.
 * Provides full CRUD operations on {@link Student} entities via {@link StudentService}.
 *
 * @author Fatima Roman
 * @version 2.0
 */
public class MainMenuStudent {

    private static final Scanner sc = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();

    /**
     * Launches the main menu loop.
     *
     * @param args command-line arguments (not used)
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = readInt();
            switch (option) {
                case 1 -> listAllStudents();
                case 2 -> findStudentById();
                case 3 -> addStudent();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 0 -> System.out.println("\nGoodbye! 🖤");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);

        sc.close();
    }

    /**
     * Prints the main menu options to the console.
     */
    private static void printMenu() {
    	System.out.println("\n=====MONSTER HIGH INSTITUTE====="
    			+ "\n1. List all students"
    			+ "\n2. Find student by ID"
    			+ "\n3. Add new student"
    			+ "\n4. Update student"
    			+ "\n5. Delete student"
    			+ "\n0. Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Retrieves and prints all students registered in the system.
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
     * Prompts for a student ID and prints the matching student.
     */
    private static void findStudentById() {
        System.out.print("Enter student ID: ");
        int id = readInt();
        try {
            Student s = studentService.findById(id);
            System.out.println("\n" + s);
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for all student fields and saves a new student.
     */
    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");

        System.out.print("ID: ");
        int id = readInt();

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Surname: ");
        String surname = sc.nextLine().trim();

        System.out.print("Birth date (YYYY-MM-DD): ");
        LocalDate birthDate = readDate();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Year (1 or 2): ");
        int year = readInt();

        System.out.print("Group name (e.g. 1A): ");
        String groupName = sc.nextLine().trim();

        System.out.print("Monster Type ID (1=Vampire, 2=Werewolf, 3=Zombie, 4=Witch, 5=Mummy): ");
        int mtId = readInt();

        MonsterType mt = new MonsterType(mtId, null, null, null, 0);

        Student newStudent = new Student(id, name, surname, birthDate, email, year, groupName, mt);

        try {
            studentService.save(newStudent);
            System.out.println("✅ Student added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    /**
     * Prompts for a student ID, loads the existing record, then allows
     * the user to enter new values for each field (leave blank to keep current).
     */
    private static void updateStudent() {
        System.out.print("\nEnter the ID of the student to update: ");
        int id = readInt();

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
        LocalDate birthDate = rawDate.isEmpty() ? existing.getBirthDate() : parseDate(rawDate);

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
            System.out.println("✅ Student updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    /**
     * Prompts for a student ID and deletes the matching record after confirmation.
     */
    private static void deleteStudent() {
        System.out.print("\nEnter student ID to delete: ");
        int id = readInt();

        try {
            Student s = studentService.findById(id);
            System.out.println("Are you sure you want to delete: " + s + " ? (y/n): ");
            String confirm = sc.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }
            studentService.deleteStudent(id);
            System.out.println("✅ Student deleted successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads a valid integer from the console, re-prompting on invalid input.
     *
     * @return the integer entered by the user
     */
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    /**
     * Reads a date in ISO format (YYYY-MM-DD), re-prompting on invalid input.
     *
     * @return the parsed {@link LocalDate}
     */
    private static LocalDate readDate() {
        while (true) {
            try {
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date, use YYYY-MM-DD format: ");
            }
        }
    }

    /**
     * Parses a date string; returns {@code null} if parsing fails.
     *
     * @param raw the raw date string
     * @return the parsed date or {@code null}
     */
    private static LocalDate parseDate(String raw) {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, keeping original.");
            return null;
        }
    }

    /**
     * Reads an optional string. Returns {@code defaultValue} if the user
     * presses Enter without typing anything.
     *
     * @param defaultValue the value to keep if the user skips input
     * @return the new value or {@code defaultValue}
     */
    private static String readOptional(String defaultValue) {
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}