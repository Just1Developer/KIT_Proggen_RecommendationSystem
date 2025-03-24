package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

/**
 * The export command outputs the current database graph, formatted as digraph, to the IO, lines sorted appropriately.
 * @author uwwfh
 */
public class ExportCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length != CommandHandler.NO_ARGUMENTS_LENGTH) {
            return CommandResult.failure(CommandHandler.ERROR_INVALID_ARGUMENTS_ZERO.formatted(args.length));
        }
        return system.getFormattedDigraph();
    }
}
