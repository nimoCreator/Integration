package pl.polsl.integration.model;

/**
 * Custom exception for errors in the IntegrationModel.
 * Author: Sebastian Legierski InfK4
 * @version 3.0 prototype
 */
public class IntegrationException extends Exception {

    /**
     * Constructor with a custom error message.
     * @param message The error message.
     */
    public IntegrationException(String message) {
        super(message);  // Pass the message to the superclass constructor
    }

    /**
     * Returns the error message.
     * @return The error message.
     */
    public String whatHappened() {
        return super.getMessage();  // Use getMessage() inherited from Exception
    }
}
