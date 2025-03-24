package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

/**
 * Quits the recommendation application.
 * @author uwwfh
 */
public class QuitCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length != CommandHandler.NO_ARGUMENTS_LENGTH) {
            return CommandResult.failure(CommandHandler.ERROR_INVALID_ARGUMENTS_ZERO.formatted(args.length));
        }
        system.quit();
        return CommandResult.success();
    }
}
