package net.justonedev.model.g;

import java.util.Objects;
import java.util.Optional;

record Edge(EdgeType edgeType, Node source, Node target) {
    String formatToSortOrder() {
        return "%s %s".formatted(source.getLabel(), target.getLabel());
    }

    public Optional<String> isValid() {
        if ((source.getType() != NodeType.PRODUCT || target.getType() != NodeType.PRODUCT)
                && (edgeType != EdgeType.CONTAINS && edgeType != EdgeType.CONTAINED_IN)) {
            return Optional.of("This relation is only available for product nodes.");
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(source, edge.source) && Objects.equals(target, edge.target) && edgeType == edge.edgeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeType, source, target);
    }
}
