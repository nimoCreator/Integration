package pl.polsl.integration.controller;

import pl.polsl.integration.model.IntegrationException;
import pl.polsl.integration.model.IntegrationModel;
import pl.polsl.integration.view.IntegrationView;

/**
 * Controller class for handling integration parameters and executing integration.
 * Author: Sebastian Legierski, InfK4
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
}