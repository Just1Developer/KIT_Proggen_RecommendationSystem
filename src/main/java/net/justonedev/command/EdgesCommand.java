package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

/**
 * The edges command outputs all current edges, line by line, to the IO, sorted appropriately.
 * @author uwwfh
 */
public class EdgesCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length != CommandHandler.NO_ARGUMENTS_LENGTH) {
            return CommandResult.failure(CommandHandler.ERROR_INVALID_ARGUMENTS_ZERO.formatted(args.length));
        }
        return system.getFormattedEdgeList();
    }
}
