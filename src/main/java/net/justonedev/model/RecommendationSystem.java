package net.justonedev.model;

import net.justonedev.command.CommandHandler;
import net.justonedev.command.CommandResult;
import net.justonedev.model.graph.EdgeData;
import net.justonedev.model.graph.DatabaseGraph;

import java.util.List;
import java.util.StringJoiner;

/**
 * The main instance for the recommendation system, and effectively a data and name abstractor for external use.
 * @author uwwfh
 */
public class RecommendationSystem {

    /**
     * The delimiter character when listing multiple elements in a single output line.
     */
    public static final String INLINE_LIST_DELIMITER = " ";

    /**
     * An immutable empty list to avoid creating new objects each time we need an empty list here.
     */
    public static final List<String> NO_DATA = List.of();
    /**
     * The error message for when no database has been loaded in yet.
     */
    public static final String GRAPH_NOT_EXISTS = "There is no database loaded";

    private static final String DIGRAPH_FORMAT_PREFIX = "digraph {";
    private static final String DIGRAPH_FORMAT_SUFFIX = "}";

    private final CommandHandler commandHandler;
    private DatabaseGraph databaseGraph;

    /**
     * Creates a new recommendation system with its respective recommendation system.
     * @param commandHandler Current command handler, used as delegate for the quit command.
     */
    public RecommendationSystem(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Loads a database graph into the system.
     * @param databaseGraph The database graph to load.
     */
    public void loadGraph(DatabaseGraph databaseGraph) {
        this.databaseGraph = databaseGraph;
    }

    /**
     * If there is currently a database loaded. Only false before the first is loaded.
     * @return True if a database is currently loaded.
     */
    public boolean hasDatabase() {
        return databaseGraph != null;
    }

    /**
     * Gets the recommendations for a given product id using a given filter strategy.
     * Returns an invalid recommendation result with an error message for displaying in IO if anything goes wrong,
     * for example if no product with the given product id exists.<br/>
     * Otherwise, gets the recommendations as a list of product labels.<br/>
     * <br/>
     * If no graph has been loaded yet, will return an invalid result with an error message.
     *
     * @param productId The product id of the source product.
     * @param recommendationStrategy The recommendation strategy.
     * @return A recommendation result of the recommendations.
     */
    public RecommendationResult getRecommendations(int productId, RecommendationStrategy recommendationStrategy) {
        if (databaseGraph == null) {
            return RecommendationResult.invalid(GRAPH_NOT_EXISTS);
        }
        return databaseGraph.getRecommendations(productId, recommendationStrategy);
    }

    /**
     * Formats the loaded database graph to a digraph format and returns a
     * successful command result with the multiline digraph as a string.<br/>
     * If no graph has been loaded yet, will return an invalid result with an error message.
     *
     * @return A multiline string of all the lines for the properly formatted digraph.
     */
    public CommandResult getFormattedDigraph() {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return CommandResult.success(formatFromStringList(List.of(DIGRAPH_FORMAT_PREFIX),
                databaseGraph.formatDigraph(), List.of(DIGRAPH_FORMAT_SUFFIX)));
    }

    /**
     * Formats the edges of the loaded database graph to the format specified in the task and returns a
     * successful command result with the multiline edge list as a string.<br/>
     * If no graph has been loaded yet, will return an invalid result with an error message.
     *
     * @return A multiline string listing all edges.
     */
    public CommandResult getFormattedEdgeList() {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return CommandResult.success(formatFromStringList(databaseGraph.formatEdges()));
    }


    /**
     * Formats the nodes of the loaded database graph to the format specified in the task and returns a
     * successful command result with the nodes listed in a single line as a string.<br/>
     * If no graph has been loaded yet, will return an invalid result with an error message.
     *
     * @return A string listing all nodes.
     */
    public CommandResult getFormattedNodeList() {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return CommandResult.success(String.join(INLINE_LIST_DELIMITER, databaseGraph.formatNodes()));
    }

    /**
     * Adds a new edge to the database, extrapolated from the given edge data.
     * Returns a command result if anything goes wrong. Attempts to create missing nodes, but will return a
     * failed result if for example the node exists with a different product id, or the product id is already
     * taken by another product.<br/>
     * Will contain a useful error message. A successful result has no message.<br/>
     * If no graph has been loaded yet, will return an invalid result with an error message.
     *
     * @param edgeData The data of the edge to add.
     * @return The result.
     */
    public CommandResult addEdgeFromData(EdgeData edgeData) {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return databaseGraph.addEdge(edgeData);
    }

    /**
     * Removes an edge from the database, extrapolated from the given edge data.
     * Returns a command result if anything goes wrong. Attempts to find and delete the given edge, but will return a
     * failed result if the edge does not exist.<br/>
     * This also deletes nodes if there are no connection to or from the node post-deletion.<br/>
     * Will contain a useful error message. A successful result has no message.<br/>
     * If no graph has been loaded yet, will return an invalid result with an error message.
     *
     * @param edgeData The data of the edge to add.
     * @return The result.
     */
    public CommandResult removeEdgeFromData(EdgeData edgeData) {
        if (databaseGraph == null) {
            return CommandResult.failure(GRAPH_NOT_EXISTS);
        }
        return databaseGraph.removeEdge(edgeData);
    }

    private static String formatFromStringList(List<String> list) {
        return formatFromStringList(NO_DATA, list, NO_DATA);
    }

    private static String formatFromStringList(List<String> prefix, List<String> list, List<String> suffix) {
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

    /**
     * Quits the application for the current {@linkplain CommandHandler}.
     */
    public void quit() {
        commandHandler.terminate();
    }
}
