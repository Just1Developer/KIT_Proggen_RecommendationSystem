package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

public class QuitCommand implements Command {

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length != 0) {
            return CommandResult.failure("Invalid number of arguments: %d (Expected 0)".formatted(args.length));
        }
        system.quit();
        return CommandResult.success();
    }
}
