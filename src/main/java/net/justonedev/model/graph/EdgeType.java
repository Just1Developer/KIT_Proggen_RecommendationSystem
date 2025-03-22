package net.justonedev.model.graph;

public enum EdgeType {
    HAS_PART("has-part", 4),
    PART_OF("part-of", 3),
    PREDECESSOR("predecessor-of", 6),
    SUCCESSOR("successor-of", 5),
    CONTAINS("contains", 1),
    CONTAINED_IN("contained-in", 2);

    private final String edgeDisplayName;
    private final int order;

    EdgeType(String edgeDisplayName, int order) {
        this.edgeDisplayName = edgeDisplayName;
        this.order = order;
    }
    
    public String getEdgeDisplayName() {
        return edgeDisplayName;
    }
    
    public String getDigraphLabelName() {
        return reformatEdgeName(edgeDisplayName);
    }

    public int getOrder() {
        return order;
    }

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
