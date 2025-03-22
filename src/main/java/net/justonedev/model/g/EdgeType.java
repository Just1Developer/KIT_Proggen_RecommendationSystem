package net.justonedev.model.g;

public enum EdgeType {
    HAS_PART("has-part"),
    PART_OF("part-of"),
    PREDECESSOR("predecessor-of"),
    SUCCESSOR("successor-of"),
    CONTAINS("contains"),
    CONTAINED_IN("contained-in");

    private final String edgeDisplayName;

    EdgeType(String edgeDisplayName) {
        this.edgeDisplayName = edgeDisplayName;
    }
    
    public String getEdgeDisplayName() {
        return edgeDisplayName;
    }
    
    public String getDigraphLabelName() {
        return reformatEdgeName(edgeDisplayName);
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
