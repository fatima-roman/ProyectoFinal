package ui;

import exceptions.SubjectNotFoundException;
import model.Subject;
import model.Teacher;
import service.SubjectService;
import service.TeacherService;
import exceptions.TeacherNotFoundException;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for subject management.
 * Provides full CRUD operations on {@link Subject} entities via {@link SubjectService}.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenuSubject {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for subject operations. */
    private static final SubjectService subjectService = new SubjectService();

    /** Service layer used to resolve teacher FKs. */
    private static final TeacherService teacherService = new TeacherService();

    /**
     * Starts the subject submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> listAllSubjects();
                case 2 -> findSubjectById();
                case 3 -> addSubject();
                case 4 -> updateSubject();
                case 5 -> deleteSubject();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
            MainMenu.savingData();

        } while (option != 0);
    }

    /**
     * Prints the subject submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== SUBJECT MANAGEMENT =====");
        System.out.println("1. List all subjects");
        System.out.println("2. Find subject by ID");
        System.out.println("3. Add new subject");
        System.out.println("4. Update subject");
        System.out.println("5. Delete subject");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Retrieves and prints all subjects.
     */
    private static void listAllSubjects() {
        List<Subject> list = subjectService.findAll();
        if (list.isEmpty()) { System.out.println("No subjects registered."); return; }
        System.out.println("\n--- Subject List (" + list.size() + " total) ---");
        list.forEach(System.out::println);
    }

    /**
     * Prompts for an ID and prints the matching subject.
     */
    private static void findSubjectById() {
        System.out.print("Enter subject ID: ");
        int id = MainMenu.readInt();
        try { System.out.println("\n" + subjectService.findById(id)); }
        catch (SubjectNotFoundException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for all subject fields and saves a new subject.
     */
    private static void addSubject() {
        System.out.println("\n--- Add New Subject ---");
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Course year (1 or 2): ");
        int course = readCourse();
        System.out.print("Teacher ID (0 = no teacher): ");
        int teacherId = MainMenu.readInt();
        Teacher teacher = null;
        if (teacherId > 0) {
            try { teacher = teacherService.findById(teacherId); }
            catch (TeacherNotFoundException e) { System.out.println("Warning: " + e.getMessage() + " Subject saved without teacher."); }
        }
        Subject s = new Subject(0, name, course, teacher);
        try { subjectService.save(s); System.out.println("Subject added successfully!"); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for an ID, loads the existing subject, then allows updating each field.
     */
    private static void updateSubject() {
        System.out.print("\nEnter the ID of the subject to update: ");
        int id = MainMenu.readInt();
        Subject existing;
        try { existing = subjectService.findById(id); }
        catch (SubjectNotFoundException e) { System.out.println("Error: " + e.getMessage()); return; }

        System.out.println("Editing: " + existing);
        System.out.println("(Press Enter to keep current value)\n");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = readOptional(existing.getName());
        System.out.print("New course [" + existing.getCourse() + "]: ");
        String rawCourse = sc.nextLine().trim();
        int course = rawCourse.isEmpty() ? existing.getCourse() : Integer.parseInt(rawCourse);
        int currentTid = existing.getTeacher() != null ? existing.getTeacher().getId() : 0;
        System.out.print("New teacher ID [" + currentTid + "] (0 = no teacher): ");
        String rawTid = sc.nextLine().trim();
        int teacherId = rawTid.isEmpty() ? currentTid : Integer.parseInt(rawTid);
        Teacher teacher = null;
        if (teacherId > 0) {
            try { teacher = teacherService.findById(teacherId); }
            catch (TeacherNotFoundException e) { System.out.println("Warning: " + e.getMessage()); }
        }
        Subject updated = new Subject(id, name, course, teacher);
        try { subjectService.update(updated); System.out.println("Subject updated successfully!"); }
        catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Prompts for an ID and deletes the subject after confirmation.
     */
    private static void deleteSubject() {
        System.out.print("\nEnter subject ID to delete: ");
        int id = MainMenu.readInt();
        try {
            Subject s = subjectService.findById(id);
            System.out.print("Delete subject \"" + s.getName() + "\"? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Deletion cancelled."); return; }
            subjectService.deleteSubject(id);
            System.out.println("Subject deleted successfully.");
        } catch (SubjectNotFoundException e) { System.out.println("Error: " + e.getMessage()); }
    }

    /**
     * Reads a course year (1 or 2), re-prompting until valid.
     *
     * @return valid course year
     */
    private static int readCourse() {
        while (true) {
            try {
                int c = Integer.parseInt(sc.nextLine().trim());
                if (c == 1 || c == 2) return c;
                System.out.print("Course must be 1 or 2: ");
            } catch (NumberFormatException e) { System.out.print("Please enter a valid number: "); }
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
