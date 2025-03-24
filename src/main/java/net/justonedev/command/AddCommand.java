package net.justonedev.command;

import net.justonedev.model.graph.EdgeData;
import net.justonedev.model.Parser;
import net.justonedev.model.RecommendationSystem;

import java.util.Optional;

/**
 * This command allows to add a new connection to the database.
 * @author uwwfh
 */
public class AddCommand implements Command {
    private static final String ERROR_EDGE_PARSE_ERROR = "Failed to parse edge: edge is not correctly formatted.";

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return performEdgeAction(args, system::addEdgeFromData);
    }

    /**
     * Performs an arbitrary action for an edge. Reconstructs the edge from the argument array, parses it,
     * and (if successful), performs the given action.
     * @param edgeParts The edge parts, as split by " ".
     * @param action The action to perform once parsed (add or remove edge, for example).
     * @return The CommandResult of this action and the edge parsing, to be returned to the CommandHandler.
     */
    static CommandResult performEdgeAction(String[] edgeParts, EdgeDataAction action) {
        String reconstructedEdge = String.join(CommandHandler.ARGUMENT_DELIMITER, edgeParts);
        Optional<EdgeData> edgeData = Parser.parseEdge(reconstructedEdge);
        if (edgeData.isEmpty()) {
            return CommandResult.failure(ERROR_EDGE_PARSE_ERROR);
        }
        return action.run(edgeData.get());
    }
}
