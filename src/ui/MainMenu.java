package ui;

import java.util.Scanner;

/**
 * Main console menu for Monster High Institute Manager.
 * This class redirects the user to each entity submenu.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class MainMenu {

    private static final Scanner sc = new Scanner(System.in);

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int option;

        do {
            printMenu();
            option = readInt();

            switch (option) {
                case 1 -> MainMenuStudent.start();
                case 2 -> MainMenuTeachers.start();
                case 3 -> MainMenuSubject.start();
                case 4 -> System.out.println("Enrollment menu coming soon...");
                case 5 -> System.out.println("Grade menu coming soon...");
                case 6 -> System.out.println("Reports menu coming soon...");
                case 7 -> System.out.println("Monster Type menu coming soon...");
                case 8 -> System.out.println("Import/Export menu coming soon...");
                case 0 -> System.out.println("\nGoodbye! 🖤");
                default -> System.out.println("Invalid option, please try again.");
            }

        } while (option != 0);

        sc.close();
    }

    /**
     * Prints the main menu.
     */
    private static void printMenu() {
        System.out.println("\n===== MONSTER HIGH INSTITUTE MANAGER =====");
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
     * Reads a valid integer from console.
     *
     * @return selected number
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
}