package ui;

import util.CsvUtil;

/**
 * Console submenu for import and export operations.
 * Delegates all file I/O to {@link CsvUtil}, which uses the default paths
 * defined in that class.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class MainMenuImportExport {

    /**
     * Starts the import/export submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> CsvUtil.exportStudents();
                case 2 -> CsvUtil.exportTeachers();
                case 3 -> CsvUtil.exportSubjects();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);
    }

    /**
     * Prints the import/export submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== IMPORT / EXPORT DATA =====");
        System.out.println("1. Export students to CSV");
        System.out.println("2. Export teachers to CSV");
        System.out.println("3. Export subjects to CSV");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }
}
