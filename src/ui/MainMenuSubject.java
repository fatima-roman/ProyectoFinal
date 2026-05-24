package ui;

import model.Subject;
import model.Teacher;
import service.SubjectService;
import service.TeacherService;

import java.util.List;
import java.util.Scanner;

/**
 * Console menu for subject management.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenuSubject {

    private static final Scanner sc = new Scanner(System.in);
    private static final SubjectService subjectService = new SubjectService();
    private static final TeacherService teacherService = new TeacherService();

    /**
     * Starts the subject submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = readInt();

            switch (option) {
                case 1 -> listAllSubjects();
                case 2 -> findSubjectById();
                case 3 -> addSubject();
                case 4 -> updateSubject();
                case 5 -> deleteSubject();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints subject menu options.
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
     * Lists all subjects.
     */
    private static void listAllSubjects() {
        List<Subject> subjects = subjectService.findAll();

        if (subjects.isEmpty()) {
            System.out.println("No subjects registered.");
            return;
        }

        System.out.println("\n--- Subject List (" + subjects.size() + " total) ---");
        subjects.forEach(System.out::println);
    }

    /**
     * Finds a subject by ID.
     */
    private static void findSubjectById() {
        System.out.print("Enter subject ID: ");
        int id = readInt();

        try {
            Subject subject = subjectService.findById(id);
            System.out.println("\n" + subject);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds a new subject.
     */
    private static void addSubject() {
        System.out.println("\n--- Add New Subject ---");

        System.out.print("ID: ");
        int id = readInt();

        System.out.print("Name: ");
        String name = readNonEmptyString();

        System.out.print("Course (1 or 2): ");
        int course = readCourse();

        System.out.print("Teacher ID: ");
        int teacherId = readInt();

        try {
            Teacher teacher = teacherService.findById(teacherId);
            Subject subject = new Subject(id, name, course, teacher);

            subjectService.save(subject);
            System.out.println("✅ Subject added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding subject: " + e.getMessage());
        }
    }

    /**
     * Updates an existing subject.
     */
    private static void updateSubject() {
        System.out.print("\nEnter the ID of the subject to update: ");
        int id = readInt();

        Subject existing;
        try {
            existing = subjectService.findById(id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.println("Editing: " + existing);
        System.out.println("(Press Enter to keep current value)\n");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = readOptional(existing.getName());

        System.out.print("New course [" + existing.getCourse() + "]: ");
        String rawCourse = sc.nextLine().trim();
        int course = rawCourse.isEmpty() ? existing.getCourse() : parseCourse(rawCourse, existing.getCourse());

        int currentTeacherId = existing.getTeacher() != null ? existing.getTeacher().getId() : -1;
        System.out.print("New teacher ID [" + currentTeacherId + "]: ");
        String rawTeacherId = sc.nextLine().trim();

        Teacher teacher = existing.getTeacher();
        if (!rawTeacherId.isEmpty()) {
            try {
                teacher = teacherService.findById(Integer.parseInt(rawTeacherId));
            } catch (Exception e) {
                System.out.println("Invalid teacher, keeping current teacher.");
            }
        }

        Subject updated = new Subject(id, name, course, teacher);

        try {
            subjectService.update(updated);
            System.out.println("✅ Subject updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating subject: " + e.getMessage());
        }
    }

    /**
     * Deletes a subject by ID.
     */
    private static void deleteSubject() {
        System.out.print("\nEnter subject ID to delete: ");
        int id = readInt();

        try {
            Subject subject = subjectService.findById(id);
            System.out.print("Are you sure you want to delete: " + subject + " ? (y/n): ");
            String confirm = sc.nextLine().trim();

            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            subjectService.deleteById(id);
            System.out.println("✅ Subject deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads a valid integer.
     *
     * @return the number entered
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
     * Reads a non-empty string.
     *
     * @return a non-empty string
     */
    private static String readNonEmptyString() {
        while (true) {
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.print("This field cannot be empty. Try again: ");
        }
    }

    /**
     * Reads course value 1 or 2.
     *
     * @return valid course
     */
    private static int readCourse() {
        while (true) {
            int course = readInt();
            if (course == 1 || course == 2) {
                return course;
            }
            System.out.print("Course must be 1 or 2. Try again: ");
        }
    }

    /**
     * Parses a course value and returns default if invalid.
     *
     * @param raw raw course text
     * @param defaultValue current value
     * @return parsed or default course
     */
    private static int parseCourse(String raw, int defaultValue) {
        try {
            int value = Integer.parseInt(raw);
            if (value == 1 || value == 2) {
                return value;
            }
        } catch (NumberFormatException e) {
            // ignored
        }

        System.out.println("Invalid course, keeping original.");
        return defaultValue;
    }

    /**
     * Reads optional text, keeping default if empty.
     *
     * @param defaultValue current value
     * @return new or existing value
     */
    private static String readOptional(String defaultValue) {
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}