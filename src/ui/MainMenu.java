package ui;

import java.util.Scanner;

import util.CsvUtil;

/**
 * Application entry point and main console menu for Monster High Institute Manager.
 * Delegates to entity-specific submenus for each management area.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenu {

    /** Shared scanner for console input. */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        CsvUtil.importMonsterTypes();
        CsvUtil.importTeachers();
        CsvUtil.importStudents();
        CsvUtil.importSubjects();

        int option;
        do {
            printMenu();
            option = readInt();
            switch (option) {
                case 1 -> MainMenuStudent.start();
                case 2 -> MainMenuTeacher.start();
                case 3 -> MainMenuSubject.start();
                case 4 -> MainMenuEnrollment.start();
                case 5 -> MainMenuGrade.start();
                case 6 -> MainMenuReports.start();
                case 7 -> MainMenuMonsterType.start();
                case 8 -> MainMenuImportExport.start();
                case 0 -> {
                    System.out.println("Saving data in CSV format...");
                    CsvUtil.updateStudents();
                    CsvUtil.updateSubjects();
                    CsvUtil.updateTeachers();
                    System.out.println("\nByee! 🖤");
                }
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
        sc.close();
    }

    /**
     * Prints the main menu to the console.
     */
    private static void printMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║  MONSTER HIGH INSTITUTE MANAGER  ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("1. Student Management");
        System.out.println("2. Teacher Management");
        System.out.println("3. Subject Management");
        System.out.println("4. Enrollment Management");
        System.out.println("5. Grade Management");
        System.out.println("6. Reports & Statistics");
        System.out.println("7. Monster Type Catalog");
        System.out.println("8. Import / Export Data");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Reads a valid integer from the console, re-prompting on invalid input.
     *
     * @return the integer entered by the user
     */
    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    /**
     * Returns the shared scanner instance.
     *
     * @return shared {@link Scanner}
     */
    static Scanner getScanner() { return sc; }
}