package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

public class ExportCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        return system.getFormattedDigraph();
    }
}
