package net.justonedev.model;

import net.justonedev.command.CommandHandler;
import net.justonedev.command.CommandResult;
import net.justonedev.model.g.EdgeData;
import net.justonedev.model.g.DatabaseGraph;

import java.util.List;
import java.util.StringJoiner;

public class RecommendationSystem {
    /**
     * An immutable empty list to avoid creating new objects each time we need an empty list here.
     */
    private static final List<String> NO_DATA = List.of();
    private static final String GRAPH_NOT_EXISTS = "There is no database loaded";

    private final CommandHandler commandHandler;
    private DatabaseGraph databaseGraph;

    public RecommendationSystem(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void loadGraph(DatabaseGraph databaseGraph) {
        this.databaseGraph = databaseGraph;
    }

    public boolean hasDatabase() {
        return databaseGraph != null;
    }

    public RecommendationResult getRecommendations(int productId, FilterStrategy filterStrategy) {
        return databaseGraph.getRecommendations(productId, filterStrategy);
    }

    public CommandResult getFormattedDigraph() {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return CommandResult.success(formatFromStringList(List.of("digraph {"), databaseGraph.formatDigraph(), List.of("}")));
    }

    public CommandResult getFormattedEdgeList() {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return CommandResult.success(formatFromStringList(databaseGraph.formatEdges()));
    }

    public String getFormattedNodeList() {
        String products = String.join(" ", databaseGraph.formatProducts());
        String categories = String.join(" ", databaseGraph.formatCategories());
        return String.join(System.lineSeparator(), products, categories);
    }

    public CommandResult addEdgeFromData(EdgeData edgeData) {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return databaseGraph.addEdge(edgeData);
    }

    public CommandResult removeEdgeFromData(EdgeData edgeData) {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return databaseGraph.removeEdge(edgeData);
    }

    private String formatFromStringList(List<String> list) {
        return formatFromStringList(NO_DATA, list, NO_DATA);
    }
    private String formatFromStringList(List<String> prefix, List<String> list, List<String> suffix) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String graphLine : prefix) {
            joiner.add(graphLine);
        }
        for (String graphLine : list) {
            joiner.add(graphLine);
        }
        for (String graphLine : suffix) {
            joiner.add(graphLine);
        }
        return joiner.toString();
    }

    public void quit() {
        commandHandler.setRunning(false);
    }
}
