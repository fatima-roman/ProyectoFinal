package ui;

import exceptions.TeacherNotFoundException;
import model.Teacher;
import service.TeacherService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for teacher management.
 * Provides full CRUD operations on {@link Teacher} entities via {@link TeacherService}.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class MainMenuTeacher {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for teacher operations. */
    private static final TeacherService teacherService = new TeacherService();

    /**
     * Starts the teacher submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> listAllTeachers();
                case 2 -> findTeacherById();
                case 3 -> addTeacher();
                case 4 -> updateTeacher();
                case 5 -> deleteTeacher();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
            MainMenu.savingData();

        } while (option != 0);
    }

    /**
     * Prints the teacher submenu options.
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

    /**
     * Retrieves and prints all teachers.
     */
    private static void listAllTeachers() {
        List<Teacher> list = teacherService.findAll();
        if (list.isEmpty()) { System.out.println("No teachers registered."); return; }
        System.out.println("\n--- Teacher List (" + list.size() + " total) ---");
        list.forEach(System.out::println);
    }

    /**
     * Prompts for an ID and prints the matching teacher.
     */
    private static void findTeacherById() {
        System.out.print("Enter teacher ID: ");
        int id = MainMenu.readInt();
        try { System.out.println("\n" + teacherService.findById(id)); }
        catch (TeacherNotFoundException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for all teacher fields and saves a new teacher.
     * Validates email format and that the birth date is not in the future.
     */
    private static void addTeacher() {
        System.out.println("\n--- Add New Teacher ---");
        System.out.print("Name: ");        String name = sc.nextLine().trim();
        System.out.print("Surname: ");     String surname = sc.nextLine().trim();
        System.out.print("Birth date (YYYY-MM-DD): "); LocalDate bd = readPastOrPresentDate();
        System.out.print("Email: ");       String email = readEmail();
        System.out.print("Specialty: ");   String specialty = sc.nextLine().trim();
        Teacher t = new Teacher(0, name, surname, bd, email, specialty);
        try { teacherService.save(t); System.out.println("Teacher added successfully!"); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for an ID, loads the existing teacher, then allows updating each field.
     * Validates email format and that the new birth date is not in the future.
     */
    private static void updateTeacher() {
        System.out.print("\nEnter the ID of the teacher to update: ");
        int id = MainMenu.readInt();
        Teacher existing;
        try { existing = teacherService.findById(id); }
        catch (TeacherNotFoundException e) { System.out.println("Error: " + e.getMessage()); return; }

        System.out.println("Editing: " + existing);
        System.out.println("(Press Enter to keep current value)\n");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = readOptional(existing.getName());
        System.out.print("New surname [" + existing.getSurname() + "]: ");
        String surname = readOptional(existing.getSurname());

        System.out.print("New birth date [" + existing.getBirthDate() + "] (YYYY-MM-DD): ");
        String rawDate = sc.nextLine().trim();
        LocalDate bd = rawDate.isEmpty()
                ? existing.getBirthDate()
                : parsePastOrPresentDate(rawDate, existing.getBirthDate());

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

        System.out.print("New specialty [" + existing.getSpecialty() + "]: ");
        String specialty = readOptional(existing.getSpecialty());

        Teacher updated = new Teacher(id, name, surname, bd, email, specialty);
        try { teacherService.update(updated); System.out.println("Teacher updated successfully!"); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for an ID and deletes the teacher after confirmation.
     */
    private static void deleteTeacher() {
        System.out.print("\nEnter teacher ID to delete: ");
        int id = MainMenu.readInt();
        try {
            Teacher t = teacherService.findById(id);
            System.out.print("Delete " + t.getName() + " " + t.getSurname() + "? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Deletion cancelled."); return; }
            teacherService.deleteTeacher(id);
            System.out.println("Teacher deleted successfully.");
        } catch (TeacherNotFoundException e) { System.out.println("Error: " + e.getMessage()); }
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
        if (at <= 0) return false;
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
                System.out.print("Invalid date, use YYYY-MM-DD: ");
            }
        }
    }

    /**
     * Attempts to parse a date that must not be in the future.
     * Returns {@code defaultValue} if parsing fails or the date is in the future.
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
