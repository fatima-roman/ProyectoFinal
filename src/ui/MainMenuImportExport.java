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
                case 1 -> {
                    CsvUtil.importMonsterTypes();
                    CsvUtil.importTeachers();
                    CsvUtil.importStudents();
                    CsvUtil.importSubjects();
                    System.out.println("All CSV files imported.");
                }
                case 2 -> CsvUtil.importStudents();
                case 3 -> CsvUtil.exportStudents();
                case 4 -> CsvUtil.importTeachers();
                case 5 -> CsvUtil.exportTeachers();
                case 6 -> CsvUtil.importSubjects();
                case 7 -> CsvUtil.exportSubjects();
                case 8 -> CsvUtil.importMonsterTypes();
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
        System.out.println("1. Import ALL from CSV (initial load)");
        System.out.println("2. Import students from CSV");
        System.out.println("3. Export students to CSV");
        System.out.println("4. Import teachers from CSV");
        System.out.println("5. Export teachers to CSV");
        System.out.println("6. Import subjects from CSV");
        System.out.println("7. Export subjects to CSV");
        System.out.println("8. Import monster types from CSV");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }
}
