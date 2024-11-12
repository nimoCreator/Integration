package pl.polsl.integration.model;

import java.util.List;

/**
 * Strategy for performing integration by dividing the area into trapezoids of specified width.
 * 
 * @version 1.0
 */
public class WidthIntegrationStrategy extends AbstractIntegrationStrategy {

    /**
     * Constructor for the width-based integration strategy.
     */
    public WidthIntegrationStrategy() {
    }

    @Override
    public double integrate(double wd, double lowerBound, double upperBound, String function, List<Pair<Double, Double>> resultTable) throws IntegrationException {
        double sum = 0.0;
        double x = lowerBound;
        resultTable.clear();

        while (x < upperBound) {
            double nextX = x + wd;
            double y = functionValue(x, function);
            double yNext = functionValue(nextX, function);

            resultTable.add(new Pair<>(x, y));
            resultTable.add(new Pair<>(nextX, yNext));

            sum += (y + yNext) * wd / 2.0;
            x = nextX;
        }
        return sum;
    }
}
