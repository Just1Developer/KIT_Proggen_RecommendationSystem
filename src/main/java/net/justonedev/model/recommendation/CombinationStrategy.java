package net.justonedev.model.recommendation;

/**
 * The combination strategy to combine different node sets when recommending products.
 * @author uwwfh
 */
public enum CombinationStrategy {
    /**
     * Single strategy, no combination.
     */
    SINGLE,
    /**
     * Combines two sets by intersecting them.
     * Result will contain only elements that are present in both sets.
     */
    INTERSECTION,
    /**
     * Combines two sets by uniting them.
     * Result will contain all unique elements from both sets.
     */
    UNION
}
