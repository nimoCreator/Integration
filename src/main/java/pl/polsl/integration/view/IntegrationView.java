package pl.polsl.integration.view;

import java.util.Scanner;

/**
 * View class for handling user interactions and displaying messages.
 * Provides methods for displaying errors, integration formatting guidance, and input prompts.
 * @author Sebastian Legierski InfK4
 * @version 3.0 final
 */
public class IntegrationView {

    private final Scanner scanner = new Scanner(System.in);
    
    /**
     * Default constructor.
     */
    public IntegrationView()
    {
        
    }

    /**
     * Displays a general informational message.
     * @param message The message to be displayed.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays error messages.
     * @param error The error message to be displayed.
     */
    public void displayError(String error) {
        System.err.println("Error: " + error);
    }

    /**
     * Displays correct formatting guidelines for input arguments.
     */
    public void displayCorrectFormatting() {
        System.out.println("Correct formatting:");
        System.out.println(" -w : width, specifies the width of the trapezoid for calculating the integration (either this or -d)");
        System.out.println(" -d : divisions, specifies the number of trapezoids the function should be divided into (either this or -w)");
        System.out.println(" -min : starting point (inclusive)");
        System.out.println(" -max : end point (inclusive)");
        System.out.println(" -f : polynomial function in format a*x^n+b*x^m + ... (no spaces)");
    }

    /**
     * Displays the result of the integration calculation.
     * @param result The integration result to be displayed.
     */
    public void displayResult(double result) {
        System.out.println("The result of trapezoidal integration is: " + result);
    }

    /**
     * Prompts the user to select a mode for integration ('d' for divisions or 'w' for width).
     * Repeats until a valid input is provided.
     * @return The selected mode as a character ('d' or 'w').
     */
    public char selectMode() {
        while (true) {
            try {
                System.out.print("Select integration mode ('d' for divisions, 'w' for width): ");
                char selectedMode = scanner.next().toLowerCase().charAt(0);

                if (selectedMode == 'd' || selectedMode == 'w') {
                    return selectedMode;
                } else {
                    throw new IllegalArgumentException("Invalid selection. Please select 'd' or 'w'.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Prompts the user to enter an integer value for divisions.
     * Repeats until a valid integer is provided.
     * @param text The text to be displayed before the input prompt.
     * @return The number of divisions as an integer.
     */
    public int getInt(String text) {
        while (true) {
            try {
                System.out.print(text);
                return Integer.parseInt(scanner.next());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    /**
     * Prompts the user to enter a double value for bounds or width.
     * Repeats until a valid double is provided.
     * @param text The text to be displayed before the input prompt.
     * @return The entered value as a double.
     */
    public double getDouble(String text) {
        while (true) {
            try {
                System.out.print(text);
                return Double.parseDouble(scanner.next());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts the user to enter a polynomial function.
     * @return The polynomial function as a String.
     */
    public String getFunction() {
        while (true) {
            scanner.nextLine();
            try {
                System.out.println("Enter the polynomial function (e.g., a*x^n + b*x^m...): ");
                String function = scanner.nextLine();
                if (function.matches("([-+]?\\d*\\*?x(\\^\\d+)?)([-+]\\d*\\*?x(\\^\\d+)?)*")) {
                    return function;
                } else {
                    throw new IllegalArgumentException("Invalid polynomial format. Please try again.");
                }
            }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
