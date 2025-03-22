package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

public class RemoveCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return AddCommand.performEdgeAction(args, system::removeEdgeFromData);
    }
}
