package pl.polsl.integration.controller;

import java.util.List;
import pl.polsl.integration.model.IntegrationException;
import pl.polsl.integration.model.IntegrationModel;
import pl.polsl.integration.model.Pair;
import pl.polsl.integration.view.IntegrationView;

/**
 * Controller class for handling integration parameters and executing integration.
 * Author: Sebastian Legierski, InfK4
 * @version 2.0
 */
public class IntegrationController {
    private final IntegrationModel integrationModel = new IntegrationModel();
    private final IntegrationView integrationView = new IntegrationView();
    
    /** 
     * Default constructor.
     * used if want to use the application in Console UI mode
     */
    public IntegrationController()
    {
    }
    
    /**
     * Constructor that initializes the controller with command-line arguments.
     * If wrong or no arguments provided, continues to COnsole UI anyways
     * @param args Command-line arguments.
     */
    public IntegrationController(String[] args) {
        if (args.length == 0) {
            integrationView.displayMessage("No parameters provided. Application with continue in console UI mode.");
        }
        
        try {
            integrationModel.readArgs(args);
        } catch (IntegrationException e) {
            integrationView.displayError(e.getMessage());
            integrationView.displayCorrectFormatting();
            integrationView.displayError("Couldn't parse the parameters. Application will continue in console UI.");
        }
    }

    /**
     * Runs the integration process, interacting with the user as needed.
     */
    public void run() {
        if (!integrationModel.isReady()) {

            // console UI mode

            integrationModel.setMode(integrationView.selectMode());
            switch (integrationModel.getMode()) {
                case 'd' -> {
                    while(true)
                    {
                        Integer divisions = integrationView.getInt("Enter number of divisions: ");
                        if(divisions < 1)
                        {
                            integrationView.displayError("Number of divisions must be a positive integer.");
                        }
                        else
                        {
                            integrationModel.setDivisions(divisions);
                            break;
                        }
                    }
                }
                case 'w' -> {
                    while (true)
                    {
                        Double width = integrationView.getDouble("Enter the width of trapezoid: ");
                        if(width <= 0)
                        {
                            integrationView.displayError("Width must be a positive number.");
                        }
                        else
                        {
                            integrationModel.setWidth(width);
                            break;
                        }
                    }
                }
                default -> {
                    integrationView.displayError("Invalid mode selected, application will terminate.");
                    return;
                }
            }

            integrationModel.setLowerBound(integrationView.getDouble("Enter the lower bound: "));
            integrationModel.setUpperBound(integrationView.getDouble("Enter the upper bound: "));

            if (integrationModel.getLowerBound() > integrationModel.getUpperBound()) {
                integrationView.displayError("Lower bound is greater than upper bound, application will flip them and proceed.");
                integrationModel.flipBounds();
            }

            integrationModel.setFunction(integrationView.getFunction());
        }
        try
        {
            double result = integrationModel.calculate();
            integrationView.displayResult(result);
        }
        catch (IntegrationException e)
        {
            integrationView.displayError(e.getMessage());
        }
    }

    /**
     * Sets the integration mode for the calculation.
     * The mode can be 'd' for divisions or 'w' for width.
     * 
     * @param c The mode character, either 'd' or 'w'.
     * @return An empty string if mode is valid; otherwise, a termination message.
     */
    public String setMode(char c) {
        if(c == 'd' || c == 'w')
        {
            integrationModel.setMode(c);
            return "";
        }
        else
            return "Invalid mode selected, application will terminate.";
    }

    /**
     * Sets the division or width value for the integration calculation based on the mode.
     * Validates that the input is a positive integer (for 'd' mode) or a positive decimal number (for 'w' mode).
     * 
     * @param text The user-provided input text for divisions or width.
     * @throws IntegrationException If the input is empty or incorrectly formatted for the selected mode.
     */
    public void setDwvalue(String text) throws IntegrationException {
        if (text.isEmpty()) throw new IntegrationException("Empty field.");

        if (integrationModel.getMode() == 'd') 
        {
            if (!text.matches("\\d+")) throw new IntegrationException("Number of divisions must be a positive integer.");
            try 
            {
                int divisions = Integer.parseInt(text);
                if (divisions < 1) throw new IntegrationException("Number of divisions must be a positive integer.");
                integrationModel.setDivisions(divisions);
            } 
            catch (NumberFormatException e) 
            {
                throw new IntegrationException("Invalid input. Please enter a positive integer.");
            }
        } 
        else 
            try 
            {
                double width = Double.parseDouble(text);
                if (width <= 0) throw new IntegrationException("Width must be a positive number.");
                integrationModel.setWidth(width);
            } 
            catch (NumberFormatException e) 
            {
                throw new IntegrationException("Invalid input. Please enter a positive decimal number.");
            }
    }


    /**
     * Sets the lower bound for the integration range.
     * Validates that the input is not empty and can be parsed as a double.
     * 
     * @param text The input text representing the lower bound.
     * @throws IntegrationException If the input is empty or incorrectly formatted.
     */
    public void setStartValue(String text) throws IntegrationException {
        if (text == null || text.trim().isEmpty()) throw new IntegrationException( "Empty field.");

        try 
        {
            Double value = Double.valueOf(text);
            integrationModel.setLowerBound(value);
        } 
        catch (NumberFormatException ex) 
        {
            throw new IntegrationException( "Incorrect format!");
        }
    }
    
    /**
     * Sets the upper bound for the integration range.
     * Validates that the input is not empty and can be parsed as a double.
     * 
     * @param text The input text representing the upper bound.
     * @throws IntegrationException If the input is empty or incorrectly formatted.
     */
    public void setEndValue(String text) throws IntegrationException {
        if("".equals(text)) throw new IntegrationException("Empty field.");
        
        try 
        {
            Double value = Double.valueOf(text);
            integrationModel.setUpperBound(value);
        } 
        catch (NumberFormatException ex) 
        {
            throw new IntegrationException("Incorrect format!");
        }
    }

   /**
     * Sets the function to be integrated, which should be a polynomial expression.
     * Validates that the input is a non-empty string and matches a polynomial format.
     * 
     * @param text The input text representing the polynomial function.
     * @throws IntegrationException If the input is empty or does not match a valid polynomial format.
     */
    public void setFunction(String text) throws IntegrationException {
        if("".equals(text)) throw new IntegrationException("Empty Field");
        
        if (!text.matches("([-+]?\\d*\\*?x(\\^\\d+)?)([-+]\\d*\\*?x(\\^\\d+)?)*")) 
            throw new IntegrationException("Invalid polynomial format.");
        else
            integrationModel.setFunction(text);
    }

    /**
     * Calculates the integral based on the specified mode, bounds, function, and divisions/width.
     * 
     * @return The result of the integration as a String.
     * @throws IntegrationException If the calculation cannot be completed due to invalid parameters.
     */
    public String calculate() throws IntegrationException 
    {
        double result = integrationModel.calculate();
        return String.valueOf(result);
    }

    /**
     * Reads and processes command-line arguments for setting up integration parameters.
     * 
     * @param args The command-line arguments containing integration parameters.
     * @throws IntegrationException If the arguments are invalid or incorrectly formatted.
     */
    public void readArgs(String[] args) throws IntegrationException 
    {
        integrationModel.readArgs(args);
    }

    /**
     * Retrieves the result table, which contains intermediate integration steps.
     * 
     * @return A list of pairs, where each pair contains an x-value and the corresponding function value.
     */
    public List<Pair<Double, Double>> getResultTable() 
    {
        return this.integrationModel.getResultTable();
    }

}