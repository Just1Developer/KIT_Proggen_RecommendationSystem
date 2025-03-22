package net.justonedev.model;

import net.justonedev.model.g.DatabaseGraph;

import java.util.List;
import java.util.Optional;

public record ParseGraphResult(Optional<DatabaseGraph> graph, List<String> validEdges) {
    private static final ParseGraphResult EMPTY_RESULT = new ParseGraphResult(Optional.empty(), List.of());

    public static ParseGraphResult empty() {
        return EMPTY_RESULT;
    }
}
