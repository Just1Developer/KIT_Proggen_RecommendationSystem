package net.justonedev.model.graph;

import java.util.Objects;
import java.util.Optional;

record Edge(EdgeType edgeType, Node source, Node target) {
    String formatToSortOrder() {
        return "%s %s".formatted(source.getName(), target.getName());
    }

    public Optional<String> checkValidity() {
        if ((source.getType() != NodeType.PRODUCT || target.getType() != NodeType.PRODUCT)
                && (edgeType != EdgeType.CONTAINS && edgeType != EdgeType.CONTAINED_IN)) {
            return Optional.of("This relation is only available for product nodes.");
        }
        if (edgeType == EdgeType.CONTAINS && source.getType() == NodeType.PRODUCT
            || edgeType == EdgeType.CONTAINED_IN && target.getType() == NodeType.PRODUCT) {
            return Optional.of("Products and categories can only be contained in categories.");
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
