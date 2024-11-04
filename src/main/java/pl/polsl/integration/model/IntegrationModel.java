package pl.polsl.integration.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model class for handling parameters and performing trapezoidal integration.
 * @author Sebastian Legierski InfK4
 */
public class IntegrationModel {

    private double width;
    private int divisions;
    private double lowerBound;
    private double upperBound;
    private String function;
    private boolean isReady = false;
    private char mode;

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
    }

    /**
     * Checks if the model is ready to calculate based on parsed parameters.
     * @return true if model is ready, false otherwise.
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Sets the mode for integration calculation ('d' for divisions, 'w' for width).
     * @param mode Character representing the mode.
     */
    public void setMode(char mode) {
        this.mode = mode;
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
     * Sets the width of trapesoids for integration.
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
    public double calculate() throws IntegrationException {
        double sum = 0.0;

        switch (mode) {
            case 'd' ->                 {
                    // Sum d trapesiods
                    
                    double step = (upperBound - lowerBound) / divisions;
                    for (int i = 0; i < divisions; i++) {
                        double x1 = lowerBound + i * step;
                        double x2 = x1 + step;
                        sum += (functionValue(x1) + functionValue(x2)) * step / 2.0;
                    }                      }
            case 'w' ->                 {
                    // Set-width trapesiods
                    
                    double step = width;
                    double x = lowerBound;
                    while (x < upperBound) {
                        double nextX = x + step;
                        sum += (functionValue(x) + functionValue(nextX)) * step / 2.0;
                        x = nextX;
                    }                      }
            default -> throw new IntegrationException("Unknown mode selected: " + mode);
        }
        return sum;
    }

    /**
     * Evaluates the polynomial function at a given x value.
     * @param x The x-value for evaluation.
     * @return The result of the function at x.
     */
    private double functionValue(double x) {
        double result = 0.0;

        Pattern termPattern = Pattern.compile("([+-]?\\d*\\.?\\d*)\\*?x\\^?(\\d*)");
        Matcher matcher = termPattern.matcher(function);

        while (matcher.find()) {
            String coefficient = matcher.group(1);
            String exponent = matcher.group(2);

            double coef = coefficient.isEmpty() || coefficient.equals("+") || coefficient.equals("-") ? (coefficient.equals("-") ? -1 : 1) : Double.parseDouble(coefficient);
            int exp = exponent.isEmpty() ? 1 : Integer.parseInt(exponent);

            result += coef * Math.pow(x, exp);
        }
        
        return result;
    }
}

