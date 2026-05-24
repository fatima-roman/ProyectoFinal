package ui;

import exceptions.GradeOutOfRangeException;
import model.Enrollment;
import service.EnrollmentService;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for grade management.
 * Allows listing, updating and querying grades of {@link Enrollment} records
 * via {@link EnrollmentService}.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenuGrade {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for enrollment / grade operations. */
    private static final EnrollmentService enrollmentService = new EnrollmentService();

    /**
     * Starts the grade submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> listAllGrades();
                case 2 -> listPassedStudents();
                case 3 -> showAverageGrade();
                case 4 -> updateGrades();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints the grade submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== GRADE MANAGEMENT =====");
        System.out.println("1. List all grades (sorted descending)");
        System.out.println("2. List passed enrollments");
        System.out.println("3. Show average grade");
        System.out.println("4. Update grades by enrollment ID");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Lists all enrollments sorted by final grade descending.
     */
    private static void listAllGrades() {
        List<Enrollment> list = enrollmentService.findAllSortedByGradeDesc();
        if (list.isEmpty()) { System.out.println("No grades registered."); return; }
        System.out.println("\n--- All Grades (highest first) ---");
        list.forEach(e -> System.out.printf("  Enrollment %-4d | %s | Grade: %.2f %s%n",
                e.getId(),
                e.getStudent() != null ? e.getStudent().getSurname() : "?",
                e.calculateFinalGrade(),
                e.hasPassed() ? "[PASS]" : "[FAIL]"));
    }

    /**
     * Lists only the enrollments where the student passed.
     */
    private static void listPassedStudents() {
        List<Enrollment> list = enrollmentService.findPassed();
        if (list.isEmpty()) { System.out.println("No passed students found."); return; }
        System.out.println("\n--- Passed Enrollments ---");
        list.forEach(System.out::println);
    }

    /**
     * Displays the average final grade across all enrollments.
     */
    private static void showAverageGrade() {
        double avg = enrollmentService.averageGrade();
        System.out.printf("%nAverage grade: %.2f%n", avg);
    }

    /**
     * Prompts for an enrollment ID and new grades to update.
     */
    private static void updateGrades() {
        System.out.print("\nEnter enrollment ID: ");
        int id = MainMenu.readInt();
        System.out.print("New grade 1 (0-10): "); double g1 = readGrade();
        System.out.print("New grade 2 (0-10): "); double g2 = readGrade();
        try {
            enrollmentService.updateGrades(id, g1, g2);
            System.out.println("Grades updated successfully!");
        } catch (GradeOutOfRangeException | IllegalArgumentException e) {
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
