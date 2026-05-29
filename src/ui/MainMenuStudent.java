package ui;

import exceptions.InvalidMonsterTypeException;
import exceptions.StudentNotFoundException;
import model.MonsterHighGroup;
import model.MonsterType;
import model.Student;
import repository.MonsterHighGroupDAO;
import service.MonsterTypeService;
import service.StudentService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for student management.
 * Provides full CRUD operations on {@link Student}
 * through {@link StudentService}.
 *
 * @author Fátima Román
 * @version 2.3
 */
public class MainMenuStudent {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for student operations. */
    private static final StudentService studentService = new StudentService();

    /** Service layer for resolving monster types. */
    private static final MonsterTypeService monsterTypeService = new MonsterTypeService();

    /** DAO for resolving group existence. */
    private static final MonsterHighGroupDAO groupDAO = new MonsterHighGroupDAO();

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
                case 0 -> System.out.println("\nReturning to the main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
            MainMenu.savingData();
        } while (option != 0);
    }

    /**
     * Displays the student submenu options.
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
     * Retrieves and prints all registered students.
     */
    private static void listAllStudents() {
        List<Student> students = studentService.findAll();
        if (students.isEmpty()) {
            System.out.println("There are no registered students.");
            return;
        }
        System.out.println("\n--- Student list (" + students.size() + " total) ---");
        students.forEach(System.out::println);
    }

    /**
     * Requests an ID and displays the corresponding student.
     */
    private static void findStudentById() {
        System.out.print("Student ID: ");
        int id = MainMenu.readInt();
        try {
            System.out.println("\n" + studentService.findById(id));
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Requests all fields and saves a new student.
     * Validates email format, birth date (must not be in the future),
     * group existence in the database, and resolves the {@link MonsterType}.
     */
    private static void addStudent() {
        System.out.println("\n--- Add New Student ---");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Surname: ");
        String surname = sc.nextLine().trim();
        System.out.print("Birth date (YYYY-MM-DD): ");
        LocalDate birthDate = readPastOrPresentDate();
        System.out.print("Email: ");
        String email = readEmail();
        System.out.print("Year (1 or 2): ");
        int year = readYear();

        String groupName = readExistingGroupName();

        System.out.println("Available monster types:");
        monsterTypeService.findAll().forEach(mt ->
            System.out.println("  " + mt.getId() + " → " + mt.getName())
        );
        System.out.print("Monster Type ID: ");
        int mtId = MainMenu.readInt();

        MonsterType mt;
        try {
            mt = monsterTypeService.findById(mtId);
        } catch (InvalidMonsterTypeException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        Student newStudent = new Student(0, name, surname, birthDate, email, year, groupName, mt);
        try {
            studentService.save(newStudent);
            System.out.println("Student added successfully!");
        } catch (Exception e) {
            System.out.println("Error while saving the student: " + e.getMessage());
        }
    }

    /**
     * Requests an ID, loads the existing record and allows modifying each field.
     * Validates email format, birth date (must not be in the future),
     * and group existence when a new group name is provided.
     */
    private static void updateStudent() {
        System.out.print("\nStudent ID to update: ");
        int id = MainMenu.readInt();
        Student existing;
        try {
            existing = studentService.findById(id);
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        System.out.println("Editing: " + existing);
        System.out.println("(Press Enter to keep the current value)\n");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = readOptional(existing.getName());
        System.out.print("New surname [" + existing.getSurname() + "]: ");
        String surname = readOptional(existing.getSurname());

        // Birth date: validate it is not in the future
        System.out.print("New birth date [" + existing.getBirthDate() + "] (YYYY-MM-DD): ");
        String rawDate = sc.nextLine().trim();
        LocalDate birthDate;
        if (rawDate.isEmpty()) {
            birthDate = existing.getBirthDate();
        } else {
            birthDate = parsePastOrPresentDate(rawDate, existing.getBirthDate());
        }

        System.out.print("New email [" + existing.getEmail() + "]: ");
        String rawEmail = sc.nextLine().trim();
        String email;
        if (rawEmail.isEmpty()) {
            email = existing.getEmail();
        } else if (isValidEmail(rawEmail)) {
            email = rawEmail;
        } else {
            System.out.println("Invalid email format. The original value will be kept.");
            email = existing.getEmail();
        }

        System.out.print("New year [" + existing.getStudentYear() + "]: ");
        String rawYear = sc.nextLine().trim();
        int year = rawYear.isEmpty() ? existing.getStudentYear() : parseIntSafe(rawYear, existing.getStudentYear());

        System.out.print("New group [" + existing.getGroupName() + "]: ");
        String rawGroup = sc.nextLine().trim();
        String groupName;
        if (rawGroup.isEmpty()) {
            groupName = existing.getGroupName();
        } else {
            groupName = validateGroupExists(rawGroup, existing.getGroupName());
        }

        int currentMtId = existing.getMonsterType() != null ? existing.getMonsterType().getId() : 0;
        System.out.print("New Monster Type ID [" + currentMtId + "]: ");
        String rawMtId = sc.nextLine().trim();
        int mtId = rawMtId.isEmpty() ? currentMtId : parseIntSafe(rawMtId, currentMtId);

        MonsterType mt;
        try {
            mt = monsterTypeService.findById(mtId);
        } catch (InvalidMonsterTypeException e) {
            System.out.println("Error: " + e.getMessage() + ". The previous type will be kept.");
            mt = existing.getMonsterType();
        }

        Student updated = new Student(id, name, surname, birthDate, email, year, groupName, mt);
        try {
            studentService.update(updated);
            System.out.println("Student updated successfully!");
        } catch (Exception e) {
            System.out.println("Error while updating: " + e.getMessage());
        }
    }

    /**
     * Requests an ID and deletes the student after confirmation.
     */
    private static void deleteStudent() {
        System.out.print("\nStudent ID to delete: ");
        int id = MainMenu.readInt();
        try {
            Student s = studentService.findById(id);
            System.out.print("Are you sure you want to delete "
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

    /**
     * Validates that an email address has a basic valid format:
     * it must contain '@', have at least one character before it,
     * and at least one '.' after it.
     *
     * @param email the email string to validate
     * @return {@code true} if the format is acceptable
     */
    private static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        int at = email.indexOf('@');
        if (at <= 0) return false;                        // '@' must not be first
        String domain = email.substring(at + 1);
        return domain.contains(".") && !domain.startsWith(".");
    }

    /**
     * Reads an email from the console, repeating until a valid format is entered.
     *
     * @return a valid email string
     */
    private static String readEmail() {
        while (true) {
            String input = sc.nextLine().trim();
            if (isValidEmail(input)) return input;
            System.out.print("Invalid email format (example: user@domain.com). Try again: ");
        }
    }

    /**
     * Reads a date from the console that is today or in the past,
     * repeating until a valid value is entered.
     *
     * @return a {@link LocalDate} that is not in the future
     */
    private static LocalDate readPastOrPresentDate() {
        while (true) {
            try {
                LocalDate date = LocalDate.parse(sc.nextLine().trim());
                if (date.isAfter(LocalDate.now())) {
                    System.out.print("The birth date cannot be in the future. Use YYYY-MM-DD: ");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date, use the format YYYY-MM-DD: ");
            }
        }
    }

    /**
     * Attempts to parse a date that must not be in the future.
     * Returns {@code defaultValue} if the format is wrong or the date is future.
     *
     * @param raw          raw date string
     * @param defaultValue fallback date
     * @return parsed date or {@code defaultValue}
     */
    private static LocalDate parsePastOrPresentDate(String raw, LocalDate defaultValue) {
        try {
            LocalDate date = LocalDate.parse(raw);
            if (date.isAfter(LocalDate.now())) {
                System.out.println("The birth date cannot be in the future. The original value will be kept.");
                return defaultValue;
            }
            return date;
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. The original value will be kept.");
            return defaultValue;
        }
    }

    /**
     * Reads a group name that already exists in the database,
     * prompting again if the name is not found.
     *
     * @return a group name that exists in the database
     */
    private static String readExistingGroupName() {
        List<MonsterHighGroup> groups = groupDAO.findAll();
        if (groups.isEmpty()) {
            System.out.println("Warning: no groups are registered in the database yet.");
            System.out.print("Group name: ");
            return sc.nextLine().trim();
        }
        System.out.println("Available groups:");
        groups.forEach(g -> System.out.println("  " + g.getName()));
        while (true) {
            System.out.print("Group name: ");
            String input = sc.nextLine().trim();
            boolean exists = groups.stream()
                    .anyMatch(g -> g.getName().equalsIgnoreCase(input));
            if (exists) return input;
            System.out.println("Group '" + input + "' does not exist. Please choose one from the list above.");
        }
    }

    /**
     * Validates that the given group name exists in the database.
     * Returns {@code defaultValue} and prints a warning if not found.
     *
     * @param groupName    the name to validate
     * @param defaultValue fallback group name
     * @return validated group name or {@code defaultValue}
     */
    private static String validateGroupExists(String groupName, String defaultValue) {
        List<MonsterHighGroup> groups = groupDAO.findAll();
        boolean exists = groups.stream()
                .anyMatch(g -> g.getName().equalsIgnoreCase(groupName));
        if (!exists) {
            System.out.println("Group '" + groupName + "' does not exist. The original group will be kept.");
            return defaultValue;
        }
        return groupName;
    }

    /**
     * Reads a valid year (1 or 2), repeating until a correct value is obtained.
     *
     * @return valid year
     */
    private static int readYear() {
        while (true) {
            try {
                int y = Integer.parseInt(sc.nextLine().trim());
                if (y == 1 || y == 2) return y;
                System.out.print("The year must be 1 or 2: ");
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    /**
     * Attempts to parse an integer; returns {@code defaultValue} if it fails.
     *
     * @param raw          number string
     * @param defaultValue fallback value
     * @return parsed integer or {@code defaultValue}
     */
    private static int parseIntSafe(String raw, int defaultValue) {
        try { return Integer.parseInt(raw.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    /**
     * Returns {@code defaultValue} if the user presses Enter without typing anything.
     *
     * @param defaultValue fallback value
     * @return user input or {@code defaultValue}
     */
    private static String readOptional(String defaultValue) {
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}
