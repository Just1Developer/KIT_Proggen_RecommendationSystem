package net.justonedev.model;

import net.justonedev.model.g.DatabaseGraph;

import java.util.List;
import java.util.Optional;

public record ParseGraphResult(Optional<DatabaseGraph> graph, List<String> validEdges, boolean valid, String errorMessage) {
    public static ParseGraphResult success(DatabaseGraph graph, List<String> validEdges) {
        return new ParseGraphResult(Optional.of(graph), validEdges, true, "");
    }

    public static ParseGraphResult failure(List<String> validEdges, String error) {
        return new ParseGraphResult(Optional.empty(), validEdges, false, error);
    }
}
