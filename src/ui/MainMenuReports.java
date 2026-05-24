package ui;

import model.Student;
import model.Teacher;
import service.EnrollmentService;
import service.StudentService;
import service.TeacherService;

import java.util.List;
import java.util.Map;

/**
 * Console submenu for reports and statistics.
 * Displays aggregated information using Stream operations from the service layer.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenuReports {

    /** Service layers used to gather data for reports. */
    private static final StudentService studentService   = new StudentService();
    private static final TeacherService teacherService   = new TeacherService();
    private static final EnrollmentService enrollService = new EnrollmentService();

    /**
     * Starts the reports submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> studentsByYear();
                case 2 -> teachersBySpecialty();
                case 3 -> topGrades();
                case 4 -> generalStats();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints the reports submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== REPORTS & STATISTICS =====");
        System.out.println("1. Students grouped by year");
        System.out.println("2. Teachers grouped by specialty");
        System.out.println("3. Top 5 grades");
        System.out.println("4. General statistics");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Groups all students by year and prints the result.
     */
    private static void studentsByYear() {
        Map<Integer, List<Student>> map = studentService.groupByYear();
        if (map.isEmpty()) { System.out.println("No students registered."); return; }
        map.forEach((year, list) -> {
            System.out.println("\n  Year " + year + " (" + list.size() + " students):");
            list.forEach(s -> System.out.println("    - " + s.getName() + " " + s.getSurname()));
        });
    }

    /**
     * Groups all teachers by specialty and prints the result.
     */
    private static void teachersBySpecialty() {
        Map<String, List<Teacher>> map = teacherService.groupBySpecialty();
        if (map.isEmpty()) { System.out.println("No teachers registered."); return; }
        map.forEach((spec, list) -> {
            System.out.println("\n  " + spec + " (" + list.size() + "):");
            list.forEach(t -> System.out.println("    - " + t.getName() + " " + t.getSurname()));
        });
    }

    /**
     * Displays the top 5 enrollments ordered by final grade descending.
     */
    private static void topGrades() {
        System.out.println("\n--- Top 5 Grades ---");
        enrollService.findAllSortedByGradeDesc().stream().limit(5).forEach(e ->
            System.out.printf("  %s → %.2f%n",
                e.getStudent() != null ? e.getStudent().getName() + " " + e.getStudent().getSurname() : "Unknown",
                e.calculateFinalGrade()));
    }

    /**
     * Prints general statistics: total counts and average grade.
     */
    private static void generalStats() {
        System.out.println("\n--- General Statistics ---");
        System.out.println("  Total students  : " + studentService.findAll().size());
        System.out.println("  Total teachers  : " + teacherService.findAll().size());
        System.out.println("  Total enrollments: " + enrollService.findAll().size());
        System.out.printf ("  Average grade   : %.2f%n", enrollService.averageGrade());
        System.out.println("  Passed students : " + studentService.countPassed());
    }
}
