package pl.polsl.integration.model;

/**
 * Custom exception for errors in the IntegrationModel.
 */
public class IntegrationException extends Exception {
    /**
     * Constructor with a custom error message.
     * @param message The error message.
     */
    public IntegrationException(String message) {
        super(message);
    }

    /**
     * Returns the error message.
     * @return The error message.
     */
    public String whatHappened() {
        return super.getMessage();
    }
}
