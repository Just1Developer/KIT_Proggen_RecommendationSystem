package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;

/**
 * The general layout for a command.
 * @author uwwfh
 */
public interface Command {
    /**
     * Executes a command.
     * @param system The Recommendation System Instance.
     * @param args The command line arguments.
     * @return The Result of the Command (Success/Failure, and optionally something to print).
     */
    CommandResult execute(RecommendationSystem system, String[] args);
}
