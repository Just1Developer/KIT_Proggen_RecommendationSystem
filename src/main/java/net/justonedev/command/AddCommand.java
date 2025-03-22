package net.justonedev.command;

import net.justonedev.model.g.EdgeData;
import net.justonedev.model.FileParser;
import net.justonedev.model.RecommendationSystem;

import java.util.Optional;
import java.util.StringJoiner;

public class AddCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return performEdgeAction(args, system::addEdgeFromData);
    }

    interface EdgeDataAction {
        CommandResult run(EdgeData edgeData);
    }

    static CommandResult performEdgeAction(String[] edgeParts, EdgeDataAction action) {
        String reconstructedEdge = String.join(" ", edgeParts);
        Optional<EdgeData> edgeData = FileParser.parseEdge(reconstructedEdge.toString());
        if (edgeData.isEmpty()) {
            return CommandResult.failure("Failed to parse edge: edge is not correctly formatted.");
        }
        return action.run(edgeData.get());
    }
}
