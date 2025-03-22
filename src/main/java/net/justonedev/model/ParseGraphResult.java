package net.justonedev.model;

import net.justonedev.model.graph.DatabaseGraph;

import java.util.ArrayList;
import java.util.List;

public record ParseGraphResult(DatabaseGraph graph, List<String> validEdges, boolean valid, String errorMessage) {
    public ParseGraphResult(DatabaseGraph graph, List<String> validEdges, boolean valid, String errorMessage) {
        this.graph = graph;
        this.validEdges = new ArrayList<>(validEdges);
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    @Override
    public List<String> validEdges() {
        return new ArrayList<>(validEdges);
    }

    public static ParseGraphResult success(DatabaseGraph graph, List<String> validEdges) {
        return new ParseGraphResult(graph, validEdges, true, "");
    }

    public static ParseGraphResult failure(List<String> validEdges, String error) {
        return new ParseGraphResult(null, validEdges, false, error);
    }
}
