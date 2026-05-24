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
 * @version 1.0
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
     */
    private static void addTeacher() {
        System.out.println("\n--- Add New Teacher ---");
        System.out.print("Name: ");        String name = sc.nextLine().trim();
        System.out.print("Surname: ");     String surname = sc.nextLine().trim();
        System.out.print("Birth date (YYYY-MM-DD): "); LocalDate bd = readDate();
        System.out.print("Email: ");       String email = sc.nextLine().trim();
        System.out.print("Specialty: ");   String specialty = sc.nextLine().trim();
        Teacher t = new Teacher(0, name, surname, bd, email, specialty);
        try { teacherService.save(t); System.out.println("Teacher added successfully!"); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for an ID, loads the existing teacher, then allows updating each field.
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
        LocalDate bd = rawDate.isEmpty() ? existing.getBirthDate() : parseDate(rawDate, existing.getBirthDate());
        System.out.print("New email [" + existing.getEmail() + "]: ");
        String email = readOptional(existing.getEmail());
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
     * @param raw          raw date string
     * @param defaultValue fallback date
     * @return parsed date or fallback
     */
    private static LocalDate parseDate(String raw, LocalDate defaultValue) {
        try { return LocalDate.parse(raw); }
        catch (DateTimeParseException e) { System.out.println("Invalid date, keeping original."); return defaultValue; }
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
