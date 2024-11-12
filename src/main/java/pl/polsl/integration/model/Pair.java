package pl.polsl.integration.model;

/**
 * A generic Pair class representing a pair of two related objects.
 * Used to store an (X, Y) pair where X and Y can be any types.
 * This is particularly useful for associating values, such as coordinates or key-value pairs.
 * 
 * @param <X> The type of the first element in the pair.
 * @param <Y> The type of the second element in the pair.
 * 
 * @author Sebastian Legierski
 * @version 2.0
 */
public class Pair<X, Y> {
    private final X x;
    private final Y y;

    /**
     * Constructs a Pair with the specified values.
     *
     * @param x the first value of the pair.
     * @param y the second value of the pair.
     */
    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the first element of the pair.
     *
     * @return the first element (of type X) of this pair.
     */
    public X getX() {
        return x;
    }

    /**
     * Retrieves the second element of the pair.
     *
     * @return the second element (of type Y) of this pair.
     */
    public Y getY() {
        return y;
    }
}
