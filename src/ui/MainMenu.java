package ui;

import exceptions.StudentNotFoundException;
import model.Student;
import service.StudentService;

import java.util.List;
import java.util.Scanner;

/**
 * Application entry point and main console menu.
 * Reads user input in a loop and delegates all operations to {@link StudentService}.
 *
 * @author Fatima
 * @version 1.0
 */
public class MainMenu {

    private static final Scanner sc = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();

    public static void main(String[] args) {
        int option;
        do {
            printMenu();
            option = readInt();

            switch (option) {
                case 1 -> listAllStudents();
                case 2 -> findStudentById();
                case 3 -> deleteStudent();
                case 0 -> System.out.println("Goodbye.");
                default -> System.out.println("Invalid option, please try again.");
            }
        } while (option != 0);

        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n=== MONSTER HIGH INSTITUTE ===");
        System.out.println("1. List all students");
        System.out.println("2. Find student by ID");
        System.out.println("3. Delete student");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void listAllStudents() {
        List<Student> students = studentService.findAll();
        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }
        students.forEach(System.out::println);
    }

    private static void findStudentById() {
        System.out.print("Enter student ID: ");
        int id = readInt();
        try {
            Student s = studentService.findById(id);
            System.out.println(s);
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        System.out.print("Enter student ID to delete: ");
        int id = readInt();
        try {
            studentService.deleteStudent(id);
            System.out.println("Student deleted successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Reads an integer from the console.
     * Keeps prompting the user if the input is not a valid number.
     * @return the integer entered by the user
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