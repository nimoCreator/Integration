package pl.polsl.integration.model;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Strategy for performing integration by dividing the area into trapezoids of specified width.
 * 
 * @version 3.0 final
 */
public class IntegrationStrategyPreciseWidth extends IntegrationStrategyAbstract {

    /**
     * Constructor for the width-based integration strategy.
     */
    public IntegrationStrategyPreciseWidth() {
    }

    @Override
    public double integrate(double wd, double lowerBound, double upperBound, String function, List<PairRecord> resultTable) throws IntegrationException {
        resultTable.clear();

        BiFunction<Double, Double, Double> trapezoidArea = (y1, y2) -> (y1 + y2) * wd / 2.0;

        double sum = 0.0;
        double x = lowerBound;

        for(int i = 0; x < upperBound; i++) 
        {
            double nextX = lowerBound + wd * (i+1);
            
            double y = functionValue(x, function);
            double yNext = functionValue(nextX, function);

            resultTable.add(new PairRecord(x, y));
            
            sum += trapezoidArea.apply(y, yNext);
            
            x = nextX ;
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
