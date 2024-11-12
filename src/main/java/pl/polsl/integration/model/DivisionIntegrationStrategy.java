package pl.polsl.integration.model;

import java.util.List;

/**
 * Strategy for performing integration by dividing the area into a specified number of trapezoids.
 * 
 * @version 1.0
 */
public class DivisionIntegrationStrategy extends AbstractIntegrationStrategy {

    /**
     * Constructor for the division-based integration strategy.
     */
    public DivisionIntegrationStrategy() {
    }

    @Override
    public double integrate(double wd, double lowerBound, double upperBound, String function, List<Pair<Double, Double>> resultTable) throws IntegrationException {
        double step = (upperBound - lowerBound) / wd;
        double sum = 0.0;
        resultTable.clear();

        for (int i = 0; i < wd; i++) {
            double x1 = lowerBound + i * step;
            double x2 = x1 + step;
            double y1 = functionValue(x1, function);
            double y2 = functionValue(x2, function);

            resultTable.add(new Pair<>(x1, y1));
            resultTable.add(new Pair<>(x2, y2));

            sum += (y1 + y2) * step / 2.0;
        }
        return sum;
    }
}
