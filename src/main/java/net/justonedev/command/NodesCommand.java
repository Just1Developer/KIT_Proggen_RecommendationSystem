package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

/**
 * The nodes command outputs all current nodes to the IO, sorted appropriately.
 * @author uwwfh
 */
public class NodesCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length != 0) {
            return CommandResult.failure("Invalid number of arguments: %d (Expected 0)".formatted(args.length));
        }
        return system.getFormattedNodeList();
    }
}
