package pl.polsl.integration.model;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Strategy for performing precise integration by dividing the area into a specified number of trapezoids.
 * 
 * @version 3.0 final
 */
public class IntegrationStrategyPreciseDivision extends IntegrationStrategyAbstract {

    /**
     * Constructor for the precise division-based integration strategy.
     */
    public IntegrationStrategyPreciseDivision() {
    }

    @Override
    public double integrate(double wd, double lowerBound, double upperBound, String function, List<PairRecord> resultTable) throws IntegrationException {
        resultTable.clear();

        double sum = 0.0;

        // Lambda to calculate trapezoid area
        BiFunction<Double, Double, Double> trapezoidArea = (y1, y2) -> (y1 + y2) * (upperBound - lowerBound) / wd / 2.0;

        for (int i = 0; i < wd; i++) {
            double x1 = lowerBound + i * (upperBound - lowerBound) / wd;
            double x2 = x1 + (upperBound - lowerBound) / wd;
            double y1 = functionValue(x1, function);
            double y2 = functionValue(x2, function);

            resultTable.add(new PairRecord(x1, y1));

            sum += trapezoidArea.apply(y1, y2);
        }
            resultTable.add( 
                new PairRecord(
                        lowerBound + (wd) * (upperBound - lowerBound) / wd, 
                        functionValue(lowerBound + (wd) * (upperBound - lowerBound) / wd, function)
                ) 
        );
        
        return sum;
    }
}
