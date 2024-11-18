package pl.polsl.integration.model;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class providing common functionality for integration strategies,
 * including polynomial function evaluation.
 * 
 * @version 3.0 prototype
 */
public abstract class AbstractIntegrationStrategy implements IntegrationStrategy {

    /**
     * Evaluates the polynomial function at a given x value.
     * 
     * @param x The x-value for evaluation.
     * @param function The polynomial function as a string.
     * @return The result of the function at x.
     */
    protected double functionValue(double x, String function) {
        Pattern termPattern = Pattern.compile("([+-]?\\d*\\.?\\d*)\\*?x\\^?(\\d*)");
        Matcher matcher = termPattern.matcher(function);

        BiFunction<Double, Integer, Double> evaluateTerm = (coef, exp) -> coef * Math.pow(x, exp);

        double result = 0.0;
        while (matcher.find()) {
            String coefficient = matcher.group(1);
            String exponent = matcher.group(2);

            double coef = coefficient.isEmpty() || coefficient.equals("+") || coefficient.equals("-") ? (coefficient.equals("-") ? -1 : 1) : Double.parseDouble(coefficient);
            int exp = exponent.isEmpty() ? 1 : Integer.parseInt(exponent);

            result += evaluateTerm.apply(coef, exp);
        }

        return result;
    }
}
