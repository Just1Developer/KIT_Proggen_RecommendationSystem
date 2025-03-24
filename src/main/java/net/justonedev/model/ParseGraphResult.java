package net.justonedev.model;

import net.justonedev.model.graph.DatabaseGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of parsing a graph. Stores (if successful) the graph itself, the list of edges to print, the validity and
 * (if not valid) the error message.
 * <br/><br/>Note that the graph object itself may be null.
 *
 * @author uwwfh
 *
 * @param graph The parsed graph, can be null.
 * @param fileLines The edges to print after parsing.
 * @param valid If the graph was parsed successfully.
 * @param errorMessage The error message if it wasn't parsed successfully.
 */
public record ParseGraphResult(DatabaseGraph graph, List<String> fileLines, boolean valid, String errorMessage) {
    /**
     * Creates a new parse graph result. Overrides the constructor because otherwise
     * we would be assigning a reference to list fields.
     * @param graph The graph. May be null.
     * @param fileLines The list of edges to print.
     * @param valid If the graph is valid.
     * @param errorMessage The error message if it isn't.
     */
    public ParseGraphResult(DatabaseGraph graph, List<String> fileLines, boolean valid, String errorMessage) {
        this.graph = graph;
        this.fileLines = new ArrayList<>(fileLines);
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the file lines. Override because of list reference returns.
     * @return A copy of the file lines.
     */
    @Override
    public List<String> fileLines() {
        return new ArrayList<>(fileLines);
    }

    /**
     * Creates a new successful parse graph result of the graph and the list of edges to print.
     * @param graph The graph. Should not be null, obviously.
     * @param edges The edges to be printed.
     * @return A new valid graph result with the given graph and edges.
     */
    public static ParseGraphResult success(DatabaseGraph graph, List<String> edges) {
        return new ParseGraphResult(graph, edges, true, "");
    }

    /**
     * Creates a new invalid parse graph result of the error message and the list of edges to print.
     * @param edges The edges to be printed.
     * @param error The error message.
     * @return A new invalid graph result with the given edges and error.
     */
    public static ParseGraphResult failure(List<String> edges, String error) {
        return new ParseGraphResult(null, edges, false, error);
    }
}
