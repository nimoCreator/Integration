package pl.polsl.integration.model;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Strategy for performing integration by dividing the area into a specified number of trapezoids.
 * 
 * @version 3.0 prototype
 */
public class DivisionIntegrationStrategy extends AbstractIntegrationStrategy {

    /**
     * Constructor for the division-based integration strategy.
     */
    public DivisionIntegrationStrategy() {
    }

    @Override
    public double integrate(double wd, double lowerBound, double upperBound, String function, List<PairRecord> resultTable) throws IntegrationException {
        resultTable.clear();

        double step = (upperBound - lowerBound) / wd;
        double sum = 0.0;

        // Lambda to calculate trapezoid area
        BiFunction<Double, Double, Double> trapezoidArea = (y1, y2) -> (y1 + y2) * step / 2.0;

        for (int i = 0; i < wd; i++) {
            double x1 = lowerBound + i * step;
            double x2 = x1 + step;
            double y1 = functionValue(x1, function);
            double y2 = functionValue(x2, function);

            resultTable.add(new PairRecord(x1, y1));

            sum += trapezoidArea.apply(y1, y2);
        }
        resultTable.add( 
                new PairRecord(
                        lowerBound + (wd) * step, 
                        functionValue(lowerBound + (wd) * step, function)
                ) 
        );
        
        return sum;
    }
}
