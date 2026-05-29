package ui;

import java.util.Scanner;

import repository.MonsterTypeDAO;
import util.CsvUtil;

/**
 * Entry point and main console menu of the Monster High Institute Manager.
 * <p>
 * On startup, it loads the initial data from CSV only if the database
 * is empty, avoiding duplicates in successive runs. On exit, it persists
 * the current state back to the CSV files.
 * </p>
 *
 * @author Fátima Román
 * @version 1.1
 */
public class MainMenu {

    /** Shared scanner for the entire console interface. */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Application entry point.
     * <p>
     * Imports the initial data from CSV only if the database is empty,
     * then displays the main menu in a loop until the user chooses to exit.
     * On exit, it automatically saves the state to the CSV files.
     * </p>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        MonsterTypeDAO mtDao = new MonsterTypeDAO();
        if (mtDao.findAll().isEmpty()) {
            CsvUtil.importMonsterTypes();
            CsvUtil.importTeachers();
            CsvUtil.importStudents();
            CsvUtil.importSubjects();
            CsvUtil.importEnrollments();
            CsvUtil.importGroups();
            CsvUtil.importSchedules();
        }

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
                case 9 ->{ try {
                	javafx.application.Application.launch(
                            ui.javafx.MonsterHighApp.class);
				}catch(IllegalStateException e) {
					System.out.println("The application can only be used once.");
				}catch (Exception e) {
					System.err.println(e);
				}}
                case 0 -> {
                    System.out.println("\nSaving data to CSV...");
                    savingData();
                    System.out.println("\nSee you later! 🖤");
                }
                default -> System.out.println("Invalid option, please try again.");
            }
            savingData();
        } while (option != 0);
        sc.close();
    }

    /**
     * Displays the main menu in the console.
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
        System.out.println("6. Reports and Statistics");
        System.out.println("7. Monster Type Catalog");
        System.out.println("8. Export Data");
        System.out.println("9. Start javafx");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Reads a valid integer from the console, repeating the prompt if the input is invalid.
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
     * Returns the shared {@link Scanner}.
     *
     * @return shared scanner instance
     */
    static Scanner getScanner() { return sc; }
    
    /**
     * Function to save data in changes 
     */
    static void savingData() {
        CsvUtil.updateStudents();
        CsvUtil.updateSubjects();
        CsvUtil.updateTeachers();
        CsvUtil.updateEnrollments();
        CsvUtil.updateGroups();
        CsvUtil.updateSchedules();
    }
}