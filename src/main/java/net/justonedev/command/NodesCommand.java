package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

public class NodesCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return CommandResult.success(system.getFormattedNodeList());
    }
}
