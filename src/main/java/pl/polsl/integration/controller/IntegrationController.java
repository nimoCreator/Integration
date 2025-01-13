package pl.polsl.integration.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.polsl.integration.model.*;
import pl.polsl.integration.view.IntegrationView;

/**
 * Controller class for handling integration parameters and executing integration.
 * Author: Sebastian Legierski, InfK4
 * @version 4.0 final
 */

public class IntegrationController {
    private final IntegrationModel integrationModel = new IntegrationModel();
    private final IntegrationView integrationView = new IntegrationView();
        
    /**
     * Default constructor for the controller.
     */
    public IntegrationController() {}

    /**
     * Constructor that initializes the controller with command-line arguments.
     * If wrong or no arguments provided, continues to COnsole UI anyways
     * @param args Command-line arguments.
     */
    @Deprecated
    public IntegrationController(String[] args) {
        if (args.length == 0) {
            integrationView.displayMessage("No parameters provided. Application with continue in console UI mode.");
        }
        
        try {
            readArgs(args);
        } catch (IntegrationException e) {
            integrationView.displayError(e.getMessage());
            integrationView.displayCorrectFormatting();
            integrationView.displayError("Couldn't parse the parameters. Application will continue in console UI.");
        }
    }

    
    /**
     * Reads command-line arguments for integration parameters and sets them in the model.
     * @param args Command-line arguments.
     * @throws IntegrationException if any required argument is missing or invalid.
     */
    public void readArgs(String[] args) throws IntegrationException {
        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                params.put(args[i], args[i + 1]);
            } else {
                throw new IntegrationException("Missing value for parameter: " + args[i]);
            }
        }

        try {
            // Validate presence of required parameters
            if (!params.containsKey("-w") && !params.containsKey("-d")) {
                throw new IntegrationException("Parameter either '-d' (divisions) or '-w' (width) is required.");
            }

            if (params.containsKey("-d")) {
                int divisions = Integer.parseInt(params.get("-d"));
                integrationModel.setDivisions(divisions); // Set divisions in the model
                integrationModel.setIntegrationStrategy(IntegrationStrategyEnum.DivisionsCount);
                
                if (divisions < 1) {
                    throw new IntegrationException("Parameter -d should be a positive integer.");
                }
            } else {
                double width = Double.parseDouble(params.get("-w"));
                integrationModel.setWidth(width); // Set width in the model
                integrationModel.setIntegrationStrategy(IntegrationStrategyEnum.TrapesoidWidth);
                
                if (width <= 0) {
                    throw new IntegrationException("Parameter -w should be a number greater than zero!");
                }
            }

            // Set lower and upper bounds
            if (!params.containsKey("-min")) {
                throw new IntegrationException("Parameter '-min' (lower bound) is required.");
            }
            double lowerBound = Double.parseDouble(params.get("-min"));
            integrationModel.setLowerBound(lowerBound);

            if (!params.containsKey("-max")) {
                throw new IntegrationException("Parameter '-max' (upper bound) is required.");
            }
            double upperBound = Double.parseDouble(params.get("-max"));
            integrationModel.setUpperBound(upperBound);

            // Set function
            if (!params.containsKey("-f")) {
                throw new IntegrationException("Parameter '-f' (function) is required.");
            }
            String function = params.get("-f");
            integrationModel.setFunction(function); // Set function in the model

            if (lowerBound > upperBound) {
                integrationModel.flipBounds(); // Flip bounds if necessary
            }

            // Validate function format
            if (!function.matches("([-+]?\\d*\\*?x(\\^\\d+)?)([-+]\\d*\\*?x(\\^\\d+)?)*")) {
                throw new IntegrationException("Invalid polynomial format.");
            }

            this.integrationModel.validate();

        } catch (NumberFormatException e) {
            throw new IntegrationException("Invalid number format in parameters: " + e.getMessage());
        }
    }
    
    
    /**
     * Runs the integration process, interacting with the user as needed.
     * @throws IntegrationException
     */
    @Deprecated
    public void run() throws IntegrationException {
        if (!integrationModel.isReady()) {

            // console UI mode

            switch(integrationView.selectMode())
            {
                case 'w' -> { integrationModel.setIntegrationStrategy(IntegrationStrategyEnum.TrapesoidWidth); }
                case 'd' -> { integrationModel.setIntegrationStrategy(IntegrationStrategyEnum.DivisionsCount); }
                default -> { throw new IntegrationException("Invalid Mode Seleted");}
            }
            switch (integrationModel.getIntegrationStrategy()) {
                case IntegrationStrategyEnum.DivisionsCount -> {
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
                case IntegrationStrategyEnum.TrapesoidWidth -> {
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
     * @param integrationStrategy Integration Strategy to be set
     * @return An empty string if mode is valid; otherwise, a termination message.
     */
    public String setIntegrationStrategy(IntegrationStrategyEnum integrationStrategy) {
        try
        {
            integrationModel.setIntegrationStrategy(integrationStrategy);
            return "";
        }
        catch (IllegalArgumentException e)
        {
            return "Invalid mode selected.";
        }
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

        if (null == integrationModel.getIntegrationStrategy()) 
            throw new IntegrationException("Invalid mode selected."); 
        
        else switch (integrationModel.getIntegrationStrategy()) {
            case DivisionsCount, PreciseDivisionsCount -> {
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
            case TrapesoidWidth, PreciseTrapesoidWidth -> {
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
            default -> throw new IntegrationException("Invalid mode selected.");
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
     * Retrieves the result table, which contains intermediate integration steps.
     * 
     * @return A list of pairs, where each pair contains an x-value and the corresponding function value.
     */
    public List<PairRecord> getResultTable() 
    {
        return this.integrationModel.getResultTable();
    }

    /**
     * Retrives range of integration
     * @return PairRecord with lower and upper bound
     */
    public PairRecord getRange() {
        return this.integrationModel.getRange();
    }

    /**
     * Retrieves the integration mode.
     * 
     * @return The integration mode as a string.
     */
    public double getW() {
        return integrationModel.getWidth();
    }

    /**
     * Retrieves the integration mode.
     * 
     * @return The integration mode as a string.
     */
    public int getD()
    {
        return integrationModel.getDivisions();
    }

    /**
     * Retrieves the lower bound of the integration range.
     * 
     * @return The lower bound as a string.
     */
    public String getFunction() {
        return integrationModel.getFunction();
    }

    /**
     * Retrieves the lower bound of the integration range.
     * @return The lower bound as a string.
     */
    public IntegrationStrategyEnum getIntegrationStrategy() {
        return integrationModel.getIntegrationStrategy();
    }
}