package net.justonedev.command;

import net.justonedev.model.graph.EdgeData;
import net.justonedev.model.FileParser;
import net.justonedev.model.RecommendationSystem;

import java.util.Optional;

public class AddCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return performEdgeAction(args, system::addEdgeFromData);
    }

    static CommandResult performEdgeAction(String[] edgeParts, EdgeDataAction action) {
        String reconstructedEdge = String.join(" ", edgeParts);
        Optional<EdgeData> edgeData = FileParser.parseEdge(reconstructedEdge);
        if (edgeData.isEmpty()) {
            return CommandResult.failure("Failed to parse edge: edge is not correctly formatted.");
        }
        return action.run(edgeData.get());
    }
}
