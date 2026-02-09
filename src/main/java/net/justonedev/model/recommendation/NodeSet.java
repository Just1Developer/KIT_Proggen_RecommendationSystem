package net.justonedev.model.recommendation;

import net.justonedev.model.RecommendationSystem;
import net.justonedev.model.stream.DataStream;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of nodes from the system. Provides functionality to list the nodes, formatted and sorted.
 * @param nodes The list of nodes. A copy will be saved.
 * @author uwwfh
 */
public record NodeSet(List<String> nodes) {
    private static final String PRODUCT_ID_FORMAT_REGEX = ":\\d+";

    /**
     * Constructs a new node set while copying the given list of nodes.
     * @param nodes The nodes for this set.
     */
    public NodeSet(List<String> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }

    /**
     * Joins another set. Adds all nodes of the other set to this set.
     * <p>
     *     Does not modify the other set, and does not create a new set.
     * </p>
     * @param other The set to join all nodes from.
     */
    public void join(NodeSet other) {
        nodes.addAll(other.nodes);
    }

    /**
     * Intersects another set. Removed all nodes from this set that are not
     * contained in the other given set.
     * <p>
     *     Does not modify the other set, and does not create a new set.
     * </p>
     * @param other The other set to intersect from.
     */
    public void intersect(NodeSet other) {
        nodes.removeIf(node -> !other.nodes.contains(node));
    }

    /**
     * Combines this set with a given other set, using the provided
     * combination strategy.
     * <p>
     *     Does not modify the other set, and does not create a new set.
     * </p>
     * @param other The other set. May be null if strategy is {@link CombinationStrategy#SINGLE}.
     * @param strategy The strategy with which to combine the two sets.
     */
    public void combine(NodeSet other, CombinationStrategy strategy) {
        switch (strategy) {
            case INTERSECTION:
                intersect(other);
                break;
            case UNION:
                join(other);
                break;
            default:
                // Other option is SINGLE, which does nothing here.
                break;
        }
    }

    /**
     * Gets all nodes of this set, listed as a single-line string, properly formatted.
     * The nodes will be sorted lexicographically by the product ID format and separated by comma.
     * @return all nodes sorted as formatted single-line string.
     */
    public String getValue() {
        return String.join(RecommendationSystem.INLINE_LIST_DELIMITER, DataStream.of(nodes).distinct().sorted(node ->
                node.replaceAll(PRODUCT_ID_FORMAT_REGEX, "")).toList());
    }
}