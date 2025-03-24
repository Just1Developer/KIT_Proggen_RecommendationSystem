package net.justonedev.model.graph;

/**
 * The type of relation (graph edge) between products.
 * @author uwwfh
 */
public enum EdgeType {
    /**
     * Target is a part of source.
     */
    HAS_PART("has-part", 4),
    /**
     * Source is a part of Target.
     */
    PART_OF("part-of", 3),
    /**
     * Target is a predecessor of Source.
     */
    PREDECESSOR("predecessor-of", 6),
    /**
     * Target is a successor of Source.
     */
    SUCCESSOR("successor-of", 5),
    /**
     * Source contains target.
     */
    CONTAINS("contains", 1),
    /**
     * Source is contained in target.
     */
    CONTAINED_IN("contained-in", 2);

    private final String edgeDisplayName;
    private final int order;

    EdgeType(String edgeDisplayName, int order) {
        this.edgeDisplayName = edgeDisplayName;
        this.order = order;
    }

    /**
     * Gets the display name of an edge type (with hyphens).
     * @return The regular edge type display name.
     */
    public String getEdgeDisplayName() {
        return edgeDisplayName;
    }

    /**
     * Gets the display name of an edge type for the digraph formatting (without hyphens).
     * @return The hyphen-less edge type display name.
     */
    public String getDigraphLabelName() {
        return reformatEdgeName(edgeDisplayName);
    }

    /**
     * Gets the order integer for the edge type, for secondary sorting.
     * @return The order index for the edge type.
     */
    public int getOrder() {
        return order;
    }

    /**
     * Parses an edge type from a string. Case-sensitive and Hyphen-insensitive.
     * @throws IllegalArgumentException If the edge type is not valid.
     * @param edgeTypeDisplayName The display name of the edge type.
     * @return The Edge type.
     */
    public static EdgeType getEdgeType(String edgeTypeDisplayName) {
        String name = reformatEdgeName(edgeTypeDisplayName);
        return switch (name) {
            case "haspart" -> EdgeType.HAS_PART;
            case "partof" -> EdgeType.PART_OF;
            case "predecessorof" -> EdgeType.PREDECESSOR;
            case "successorof" -> EdgeType.SUCCESSOR;
            case "contains" -> EdgeType.CONTAINS;
            case "containedin" -> EdgeType.CONTAINED_IN;
            default -> throw new IllegalArgumentException("Unknown edge type: " + edgeTypeDisplayName);
        };
    }

    /**
     * Gets the inverse type of a given edge type. For example, the inverse of CONTAINS is CONTAINED_IN.
     * @param edgeType The edge type.
     * @return Its inverse.
     */
    public static EdgeType getInverseType(EdgeType edgeType) {
        return switch (edgeType) {
            case HAS_PART -> EdgeType.PART_OF;
            case PART_OF -> EdgeType.HAS_PART;
            case PREDECESSOR -> EdgeType.SUCCESSOR;
            case SUCCESSOR -> EdgeType.PREDECESSOR;
            case CONTAINS -> EdgeType.CONTAINED_IN;
            case CONTAINED_IN -> EdgeType.CONTAINS;
        };
    }

    private static String reformatEdgeName(String edgeDisplayName) {
        return edgeDisplayName.replace("-", "");
    }
}
