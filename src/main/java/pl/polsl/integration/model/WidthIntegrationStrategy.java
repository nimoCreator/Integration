package pl.polsl.integration.model;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Strategy for performing integration by dividing the area into trapezoids of specified width.
 * 
 * @version 3.0 final
 */
public class WidthIntegrationStrategy extends AbstractIntegrationStrategy {

    /**
     * Constructor for the width-based integration strategy.
     */
    public WidthIntegrationStrategy() {
    }

    @Override
    public double integrate(double wd, double lowerBound, double upperBound, String function, List<PairRecord> resultTable) throws IntegrationException {
        resultTable.clear();

        BiFunction<Double, Double, Double> trapezoidArea = (y1, y2) -> (y1 + y2) * wd / 2.0;

        double sum = 0.0;
        double x = lowerBound;

        while (x < upperBound) {
            double nextX = x + wd;
            
            double y = functionValue(x, function);
            double yNext = functionValue(nextX, function);

            resultTable.add(new PairRecord(x, y));
            
            sum += trapezoidArea.apply(y, yNext);
            x = nextX;
        }
        resultTable.add( 
                new PairRecord(
                        x, 
                        functionValue(x, function)
                ) 
        );
        
        return sum;
    }
}
