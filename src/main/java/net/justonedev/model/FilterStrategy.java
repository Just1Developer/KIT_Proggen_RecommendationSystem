package net.justonedev.model;

public enum FilterStrategy {
    /**
     * S1.
     */
    SIBLING,
    /**
     * S2.
     */
    SUCCESSOR,
    /**
     * S3.
     */
    PREDECESSOR;

    public static FilterStrategy fromId(int id) {
        return switch (id) {
            case 1 -> SIBLING;
            case 2 -> SUCCESSOR;
            case 3 -> PREDECESSOR;
            default -> throw new IllegalArgumentException("Unknown FilterStrategy: " + id);
        };
    }
}
