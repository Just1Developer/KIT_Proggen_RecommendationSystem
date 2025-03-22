package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

public class QuitCommand implements Command {

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        system.quit();
        return CommandResult.success();
    }
}
