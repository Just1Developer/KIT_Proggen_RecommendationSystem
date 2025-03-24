package net.justonedev.model;

/**
 * The different recommendation strategies for recommendations.
 * @author uwwfh
 */
public enum RecommendationStrategy {
    /**
     * The sibling strategy, dubbed S1. Recommends all products which are contained in a same category as the target.
     */
    SIBLING(1),
    /**
     * The successor strategy, dubbed S2. Recommends all products which are direct and indirect successors of the target product.
     */
    SUCCESSOR(2),
    /**
     * The predecessor strategy, dubbed S3. Recommends all products which are direct and indirect predecessors of the target product.
     */
    PREDECESSOR(3);

    private static final String UNKNOWN_FILTER_STRATEGY = "Unknown FilterStrategy Id: %d";

    private final int strategyIndex;

    RecommendationStrategy(int strategyIndex) {
        this.strategyIndex = strategyIndex;
    }

    /**
     * Parses the strategy from its respective id (used in S1, S2, S3). If the id is not valid, throws an IllegalArgumentException.
     * @throws IllegalArgumentException if the id is invalid (not between 1 and 3).
     * @param id The strategy id.
     * @return The FilterStrategy with that id.
     */
    public static RecommendationStrategy fromId(int id) {
        for (RecommendationStrategy strategy : values()) {
            if (strategy.strategyIndex == id) {
                return strategy;
            }
        }
        throw new IllegalArgumentException(UNKNOWN_FILTER_STRATEGY.formatted(id));
    }
}
