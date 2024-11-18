package pl.polsl.integration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import lombok.*;

/**
 * Model class for handling parameters and performing trapezoidal integration.
 * @author Sebastian Legierski InfK4
 * @version 3.0 prototype
 */
//@AllArgsConstructor
//@Getter
//@Setter
//@EqualsAndHashCode
//@ToString
public class IntegrationModel {

    private double width;
    private int divisions;
    private double lowerBound;
    private double upperBound;
    private String function;
    private boolean isReady = false;
    private char mode;
    private final List<PairRecord> resultTable = new ArrayList<>();
    private IntegrationStrategy strategy;
    
    /**
     * Default constructor.
     */
    public IntegrationModel()
    {
        
    }
    
    /**
     * Reads command-line arguments for integration parameters.
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
            
            if ( !params.containsKey("-w") && !params.containsKey("-d") ) {
                throw new IntegrationException("Parameter either '-d' (divisions) or '-w' (width) is required.");
            }
            
            if (params.containsKey("-d")) {
                divisions = Integer.parseInt(params.get("-d"));
                mode = 'd';
                
                if(divisions < 1)
                    throw new IntegrationException("Parameter -d shold be a positive integer.");
            }
            else
            {
                width = Double.parseDouble(params.get("-w"));
                mode = 'w';
                if(width < 0)
                    throw new IntegrationException("Parameter -w cannot be negative!");
            }
            
            if (!params.containsKey("-min")) {
                throw new IntegrationException("Parameter '-min' (lower bound) is required.");
            }
            lowerBound = Double.parseDouble(params.get("-min"));

            if (!params.containsKey("-max")) {
                throw new IntegrationException("Parameter '-max' (upper bound) is required.");
            }
            upperBound = Double.parseDouble(params.get("-max"));

            if (!params.containsKey("-f")) {
                throw new IntegrationException("Parameter '-f' (function) is required.");
            }
            function = params.get("-f");
            
            if( lowerBound > upperBound )
                flipBounds();

            if (!function.matches("([-+]?\\d*\\*?x(\\^\\d+)?)([-+]\\d*\\*?x(\\^\\d+)?)*")) 
            {
                throw new IntegrationException("Invalid polynomial format.");
            }

            isReady = true;

        } catch (NumberFormatException e) {
            throw new IntegrationException("Invalid number format in parameters: " + e.getMessage());
        }
    }

    /**
     * Checks if the model is ready to calculate based on parsed parameters.
     * @return true if model is ready, false otherwise.
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Sets the integration mode by creating the appropriate strategy.
     * 
     * @param mode Character representing the mode ('d' for divisions, 'w' for width).
     */
    public void setMode(char mode) {
        if (mode == 'd') {
            this.setStrategy(new DivisionIntegrationStrategy());
        } else if (mode == 'w') {
            this.setStrategy(new WidthIntegrationStrategy());
        }
    }
    /**
     * Sets the integration strategy to be used.
     * @param strategy The integration strategy.
     */
    public void setStrategy(IntegrationStrategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Returns the mode of integration calculation.
     * @return The integration mode.
     */
    public char getMode() {
        return mode;
    }

    /**
     * Sets the number of divisions.
     * @param divisions Number of trapezoids.
     */
    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    /**
     * Sets the width of trapezoids for integration.
     * @param width Desired precision level, lower the value - higher the precision.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Sets the lower bound for integration.
     * @param lowerBound The starting point.
     */
    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * Sets the upper bound for integration.
     * @param upperBound The ending point.
     */
    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * Flips the lower and upper bounds if needed.
     */
    public void flipBounds() {
        upperBound = upperBound + lowerBound;
        lowerBound = upperBound - lowerBound;
        upperBound = lowerBound - upperBound;
    }

        /**
     * Gets the lower bound for integration.
     * @return The lower bound.
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * Gets the upper bound for integration.
     * @return The upper bound.
     */
    public double getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the polynomial function to integrate.
     * @param function A polynomial in the format "a*x^n + b*x^m + ...".
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * Calculates the result of trapezoidal integration.
     * @return The integration result.
     * @throws IntegrationException if an unknown mode is selected.
     */
        /**
     * Calculates the integration result using the selected strategy.
     * 
     * @return The result of the integration.
     * @throws IntegrationException if no strategy is set.
     */
    public double calculate() throws IntegrationException {
        if (strategy == null) {
            throw new IntegrationException("Integration strategy not set.");
        }
        // double wd, double lowerBound, double upperBound, String function, List<Pair<Double, Double>> resultTable
        double wd = this.mode == 'd' ? this.divisions : this.width;
        return strategy.integrate(wd, lowerBound, upperBound, function, resultTable);
    }
    
    /**
     * Returns the List o X and Y's of the function after calculating
     * @return List of Pairs of X's and Y's being the result of integration of the INtegration
     */
    public List<PairRecord> getResultTable() {
        return resultTable;
    }
}

