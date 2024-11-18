package pl.polsl.integration.model;

import java.util.List;

/**
 * Interface for integration strategies, allowing different approaches to integration.
 * 
 * @version 3.0 prototype
 */
public interface IntegrationStrategy {
    
    /**
     * Integrates the function over a specified range.
     * 
     * @param wd Either width or count of divisions in integration
     * @param lowerBound The lower bound of integration.
     * @param upperBound The upper bound of integration.
     * @param function The polynomial function as a string.
     * @param resultTable List to store pairs of x and y values for visualization or output.
     * @return The result of the integration.
     * @throws IntegrationException If any error occurs during integration.
     */
    double integrate(double wd, double lowerBound, double upperBound, String function, List<PairRecord> resultTable) throws IntegrationException;
}
