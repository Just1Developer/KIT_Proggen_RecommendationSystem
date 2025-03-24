package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

/**
 * This command allows to remove an existing connection from the database.
 * @author uwwfh
 */
public class RemoveCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return AddCommand.performEdgeAction(args, system::removeEdgeFromData);
    }
}
