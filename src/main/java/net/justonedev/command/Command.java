package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

public interface Command {
    CommandResult execute(RecommendationSystem system, String[] args);
}
