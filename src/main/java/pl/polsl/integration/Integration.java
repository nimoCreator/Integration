package pl.polsl.integration;

import pl.polsl.integration.GUI.IntegrationJFrame;

/**
 * Main class to initiate the integration application.
 * @author Sebastian Legierski InfK4
 * @version 4.0 final
 */
public class Integration {

    /**
     * Default constructor.
     */
    public Integration()
    {
                
    }
    
    /**
     * Entry point of the application. Initializes and runs the IntegrationController.
     * @param args Command-line arguments.
     * arguments formatting:
     * -w : width, specifies the width of the trapezoid for calculating the integration (either this or -d)
     * -d : divisions, specifies the number of trapezoids the function should be divided into (either this or -w)
     * -min : starting point (inclusive)
     * -max : end point (inclusive)
     * -f : polynomial function in format a*x^n + b*x^m + ... (no spaces)
     */
    public static void main(String[] args) {
        IntegrationJFrame.start(args);      
    }
}
