package ui;

import exceptions.DuplicateEnrollmentException;
import exceptions.GradeOutOfRangeException;
import exceptions.StudentNotFoundException;
import exceptions.SubjectNotFoundException;
import model.Enrollment;
import service.EnrollmentService;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for enrollment management.
 * Provides full CRUD and grade-update operations on {@link Enrollment} entities
 * via {@link EnrollmentService}.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenuEnrollment {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for enrollment operations. */
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    /**
     * Starts the enrollment submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> listAllEnrollments();
                case 2 -> findByStudentId();
                case 3 -> enrollStudent();
                case 4 -> updateGrades();
                case 5 -> deleteEnrollment();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints the enrollment submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== ENROLLMENT MANAGEMENT =====");
        System.out.println("1. List all enrollments");
        System.out.println("2. Find enrollments by student ID");
        System.out.println("3. Enroll student in subject");
        System.out.println("4. Update grades");
        System.out.println("5. Delete enrollment");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Retrieves and prints all enrollments.
     */
    private static void listAllEnrollments() {
        List<Enrollment> list = enrollmentService.findAll();
        if (list.isEmpty()) { System.out.println("No enrollments registered."); return; }
        System.out.println("\n--- Enrollment List (" + list.size() + " total) ---");
        list.forEach(System.out::println);
    }

    /**
     * Prompts for a student ID and prints all their enrollments.
     */
    private static void findByStudentId() {
        System.out.print("Enter student ID: ");
        int sid = MainMenu.readInt();
        List<Enrollment> list = enrollmentService.findByStudentId(sid);
        if (list.isEmpty()) { System.out.println("No enrollments found for student ID " + sid + "."); return; }
        list.forEach(System.out::println);
    }

    /**
     * Prompts for student ID, subject ID and grades, then creates a new enrollment.
     */
    private static void enrollStudent() {
        System.out.println("\n--- Enroll Student in Subject ---");
        System.out.print("Student ID: ");       int sid = MainMenu.readInt();
        System.out.print("Subject ID: ");       int subId = MainMenu.readInt();
        System.out.print("Grade 1 (0-10): ");   double g1 = readGrade();
        System.out.print("Grade 2 (0-10): ");   double g2 = readGrade();
        try {
            enrollmentService.enroll(sid, subId, g1, g2);
            System.out.println("Enrollment created successfully!");
        } catch (StudentNotFoundException | SubjectNotFoundException |
                 GradeOutOfRangeException | DuplicateEnrollmentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for an enrollment ID and new grades, then updates the record.
     */
    private static void updateGrades() {
        System.out.print("\nEnter enrollment ID to update: ");
        int id = MainMenu.readInt();
        System.out.print("New grade 1 (0-10): "); double g1 = readGrade();
        System.out.print("New grade 2 (0-10): "); double g2 = readGrade();
        try {
            enrollmentService.updateGrades(id, g1, g2);
            System.out.println("Grades updated successfully!");
        } catch (GradeOutOfRangeException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for an enrollment ID and deletes it after confirmation.
     */
    private static void deleteEnrollment() {
        System.out.print("\nEnter enrollment ID to delete: ");
        int id = MainMenu.readInt();
        try {
            Enrollment e = enrollmentService.findById(id);
            System.out.print("Delete enrollment (id=" + e.getId() + ")? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) { System.out.println("Deletion cancelled."); return; }
            enrollmentService.deleteEnrollment(id);
            System.out.println("Enrollment deleted successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Reads a grade value in [0, 10], re-prompting until valid.
     *
     * @return valid grade value
     */
    private static double readGrade() {
        while (true) {
            try {
                double g = Double.parseDouble(sc.nextLine().trim());
                if (g >= 0 && g <= 10) return g;
                System.out.print("Grade must be between 0 and 10: ");
            } catch (NumberFormatException e) { System.out.print("Please enter a valid number: "); }
        }
    }
}
