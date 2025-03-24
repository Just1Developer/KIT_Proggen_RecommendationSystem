package net.justonedev.model;

/**
 * The different recommendation strategies for recommendations.
 * @author uwwfh
 */
public enum RecommendationStrategy {
    /**
     * The sibling strategy, dubbed S1. Recommends all products which are contained in a same category as the target.
     */
    SIBLING,
    /**
     * The successor strategy, dubbed S2. Recommends all products which are direct and indirect successors of the target product.
     */
    SUCCESSOR,
    /**
     * The predecessor strategy, dubbed S3. Recommends all products which are direct and indirect predecessors of the target product.
     */
    PREDECESSOR;

    /**
     * Parses the strategy from its respective id (used in S1, S2, S3). If the id is not valid, throws an IllegalArgumentException.
     * @throws IllegalArgumentException if the id is invalid (not between 1 and 3).
     * @param id The strategy id.
     * @return The FilterStrategy with that id.
     */
    public static RecommendationStrategy fromId(int id) {
        return switch (id) {
            case 1 -> SIBLING;
            case 2 -> SUCCESSOR;
            case 3 -> PREDECESSOR;
            default -> throw new IllegalArgumentException("Unknown FilterStrategy: " + id);
        };
    }
}
