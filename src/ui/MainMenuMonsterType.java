package ui;

import exceptions.InvalidMonsterTypeException;
import model.MonsterType;
import service.MonsterTypeService;

import java.util.List;
import java.util.Scanner;

/**
 * Console submenu for the Monster Type catalogue.
 * Provides full CRUD operations on {@link MonsterType} entities via {@link MonsterTypeService}.
 *
 * @author Fatima Roman
 * @version 1.1
 */
public class MainMenuMonsterType {

    /** Shared scanner from the main menu. */
    private static final Scanner sc = MainMenu.getScanner();

    /** Service layer for monster type operations. */
    private static final MonsterTypeService monsterTypeService = new MonsterTypeService();

    /**
     * Starts the monster type submenu loop.
     */
    public static void start() {
        int option;
        do {
            printMenu();
            option = MainMenu.readInt();
            switch (option) {
                case 1 -> listAll();
                case 2 -> findById();
                case 3 -> add();
                case 4 -> update();
                case 5 -> delete();
                case 6 -> showStats();
                case 0 -> System.out.println("\nReturning to main menu...");
                default -> System.out.println("Invalid option, please try again.");
            }
            MainMenu.savingData();

        } while (option != 0);
    }

    /**
     * Prints the monster type submenu options.
     */
    private static void printMenu() {
        System.out.println("\n===== MONSTER TYPE CATALOG =====");
        System.out.println("1. List all monster types");
        System.out.println("2. Find by ID");
        System.out.println("3. Add new monster type");
        System.out.println("4. Update monster type");
        System.out.println("5. Delete monster type");
        System.out.println("6. Show terror level statistics");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
    }

    /**
     * Retrieves and prints all monster types sorted by name.
     */
    private static void listAll() {
        List<MonsterType> list = monsterTypeService.findAllSortedByName();
        if (list.isEmpty()) { System.out.println("No monster types registered."); return; }
        System.out.println("\n--- Monster Type Catalog (" + list.size() + " types) ---");
        list.forEach(System.out::println);
    }

    /**
     * Prompts for an ID and prints the matching monster type.
     */
    private static void findById() {
        System.out.print("Enter monster type ID: ");
        int id = MainMenu.readInt();
        try {
            System.out.println("\n" + monsterTypeService.findById(id));
        } catch (InvalidMonsterTypeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for all fields and saves a new monster type.
     */
    private static void add() {
        System.out.println("\n--- Add Monster Type ---");
        System.out.print("Name: ");        String name = sc.nextLine().trim();
        System.out.print("Description: "); String desc = sc.nextLine().trim();
        System.out.print("Weakness: ");    String weak = sc.nextLine().trim();
        System.out.print("Terror level (1-10): ");
        int terror = readTerror();
        MonsterType mt = new MonsterType(0, name, desc, weak, terror);
        try {
            monsterTypeService.save(mt);
            System.out.println("Monster type added successfully! (assigned id=" + mt.getId() + ")");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for an ID, loads the existing monster type, then allows updating each field.
     */
    private static void update() {
        System.out.print("\nEnter ID to update: ");
        int id = MainMenu.readInt();
        MonsterType existing;
        try {
            existing = monsterTypeService.findById(id);
        } catch (InvalidMonsterTypeException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        System.out.println("Editing: " + existing);
        System.out.println("(Press Enter to keep current value)\n");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = readOptional(existing.getName());
        System.out.print("New description [" + existing.getDescription() + "]: ");
        String desc = readOptional(existing.getDescription());
        System.out.print("New weakness [" + existing.getWeakness() + "]: ");
        String weak = readOptional(existing.getWeakness());
        System.out.print("New terror level [" + existing.getTerrorLevel() + "]: ");
        String rawTerror = sc.nextLine().trim();
        int terror = rawTerror.isEmpty() ? existing.getTerrorLevel() : Integer.parseInt(rawTerror);

        MonsterType updated = new MonsterType(id, name, desc, weak, terror);
        try {
            monsterTypeService.update(updated);
            System.out.println("Monster type updated successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prompts for an ID and deletes the monster type after confirmation.
     */
    private static void delete() {
        System.out.print("\nEnter ID to delete: ");
        int id = MainMenu.readInt();
        try {
            MonsterType mt = monsterTypeService.findById(id);
            System.out.print("Delete \"" + mt.getName() + "\"? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }
            monsterTypeService.deleteById(id);
            System.out.println("Monster type deleted successfully.");
        } catch (InvalidMonsterTypeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Displays terror level statistics for the catalogue.
     */
    private static void showStats() {
        System.out.printf("%nAverage terror level: %.2f%n", monsterTypeService.averageTerrorLevel());
        System.out.println("All types by name:");
        monsterTypeService.getAllNames().forEach(n -> System.out.println("  - " + n));
    }

    /**
     * Reads a terror level value in [1, 10], re-prompting until valid.
     *
     * @return valid terror level
     */
    private static int readTerror() {
        while (true) {
            try {
                int t = Integer.parseInt(sc.nextLine().trim());
                if (t >= 1 && t <= 10) return t;
                System.out.print("Terror level must be between 1 and 10: ");
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    /**
     * Returns {@code defaultValue} if the user presses Enter without typing anything.
     *
     * @param defaultValue the fallback value
     * @return user input or {@code defaultValue}
     */
    private static String readOptional(String defaultValue) {
        String input = sc.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }
}
