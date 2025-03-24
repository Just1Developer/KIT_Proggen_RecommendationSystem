package net.justonedev.model.graph;

import java.util.Objects;
import java.util.Optional;

/**
 * An edge in the database graph. Has a source, target and type of edge.
 *
 * @author uwwfh
 *
 * @param edgeType The type of the edge.
 * @param source The source node (origin).
 * @param target The target node (destination).
 */
record Edge(EdgeType edgeType, Node source, Node target) {
    private static final String FORMAT_SORT_ORDER = "%s %s";
    private static final String ERROR_PRODUCT_ONLY_RELATION = "This relation is only available for product nodes.";
    private static final String ERROR_CONTAINS_RELATION = "Products and categories can only be contained in categories.";

    /**
     * Formats the edge to a string by which we can then naturally sort to get the proper order.
     * @return The string which, when sorted by, produces the required order.
     */
    String formatToSortOrder() {
        return FORMAT_SORT_ORDER.formatted(source.getName(), target.getName());
    }

    /**
     * Checks if this edge would be valid. Specifically, compares the edge type with the types of the source and target
     * nodes and confirms that this would be an allowed connection. For example, a product cannot be contained in
     * another product.<br/>
     * In this case, an error message is returned. If the returned optional is empty, the edge is valid.
     * @return An optional error message, if there is no error message, the edge is valid.
     */
    public Optional<String> checkValidity() {
        if ((source.getType() != NodeType.PRODUCT || target.getType() != NodeType.PRODUCT)
                && (edgeType != EdgeType.CONTAINS && edgeType != EdgeType.CONTAINED_IN)) {
            return Optional.of(ERROR_PRODUCT_ONLY_RELATION);
        }
        if (edgeType == EdgeType.CONTAINS && source.getType() == NodeType.PRODUCT
            || edgeType == EdgeType.CONTAINED_IN && target.getType() == NodeType.PRODUCT) {
            return Optional.of(ERROR_CONTAINS_RELATION);
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return Objects.equals(source, edge.source) && Objects.equals(target, edge.target) && edgeType == edge.edgeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeType, source, target);
    }
}
