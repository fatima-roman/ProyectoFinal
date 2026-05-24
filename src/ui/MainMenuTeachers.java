package ui;

import model.Teacher;
import service.TeacherService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu for teacher management.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenuTeachers {

    private static final Scanner sc = new Scanner(System.in);
    private static final TeacherService teacherService = new TeacherService();

    /**
     * Starts the teacher submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = readInt();

            switch (option) {
                case 1 -> listAllTeachers();
                case 2 -> findTeacherById();
                case 3 -> addTeacher();
                case 4 -> updateTeacher();
                case 5 -> deleteTeacher();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints teacher menu options.
     */
    private static void printMenu() {
        System.out.println("\n===== TEACHER MANAGEMENT =====");
        System.out.println("1. List all teachers");
        System.out.println("2. Find teacher by ID");
        System.out.println("3. Add new teacher");
        System.out.println("4. Update teacher");
        System.out.println("5. Delete teacher");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    private static void listAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();
        if (teachers.isEmpty()) {
            System.out.println("No teachers registered.");
            return;
        }

        System.out.println("\n--- Teacher List (" + teachers.size() + " total) ---");
        teachers.forEach(System.out::println);
    }

    private static void findTeacherById() {
        System.out.print("Enter teacher ID: ");
        int id = readInt();

        try {
            Teacher teacher = teacherService.findById(id);
            System.out.println("\n" + teacher);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addTeacher() {
        System.out.println("\n--- Add New Teacher ---");

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

        System.out.print("Specialty: ");
        String specialty = sc.nextLine().trim();

        Teacher teacher = new Teacher(id, name, surname, birthDate, email, specialty);

        try {
            teacherService.save(teacher);
            System.out.println("✅ Teacher added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding teacher: " + e.getMessage());
        }
    }

    private static void updateTeacher() {
        System.out.print("\nEnter the ID of the teacher to update: ");
        int id = readInt();

        Teacher existing;
        try {
            existing = teacherService.findById(id);
        } catch (Exception e) {
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

        System.out.print("New specialty [" + existing.getSpecialty() + "]: ");
        String specialty = readOptional(existing.getSpecialty());

        Teacher updated = new Teacher(id, name, surname, birthDate, email, specialty);

        try {
            teacherService.update(updated);
            System.out.println("✅ Teacher updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating teacher: " + e.getMessage());
        }
    }

    private static void deleteTeacher() {
        System.out.print("\nEnter teacher ID to delete: ");
        int id = readInt();

        try {
            Teacher teacher = teacherService.findById(id);
            System.out.print("Are you sure you want to delete: " + teacher + " ? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            teacherService.deleteById(id);
            System.out.println("✅ Teacher deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static LocalDate readDate() {
        while (true) {
            try {
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date, use YYYY-MM-DD format: ");
            }
        }
    }

    private static LocalDate parseDate(String raw, LocalDate defaultValue) {
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, keeping original.");
            return defaultValue;
        }
    }

    private static String readOptional(String defaultValue) {
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}