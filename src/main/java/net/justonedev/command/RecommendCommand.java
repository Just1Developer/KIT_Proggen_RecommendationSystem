package net.justonedev.command;

import net.justonedev.model.RecommendationSystem;
import net.justonedev.model.recommendation.ConstructSetResult;

/**
 * The recommend command gives recommendations for a given product id using specified recommendation strategies (three available).
 * The command also allows for combination of these recommendation strategies using set intersection and set union.
 * @author uwwfh
 */
public class RecommendCommand implements Command {

    private static final String PRINT_EMPTY_LINE_TRIGGER = " ";

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (!system.hasDatabase()) {
            return CommandResult.failure(RecommendationSystem.GRAPH_NOT_EXISTS);
        }
        String setPattern = String.join(CommandHandler.ARGUMENT_DELIMITER, args);
        ConstructSetResult constructSetResult = ConstructSetResult.constructSet(system, setPattern);
        if (constructSetResult.isInvalid()) {
            return CommandResult.failure(constructSetResult.getErrors());
        }
        String recommendations = constructSetResult.getRecommendations();
        // Space such that an empty line is printed when there are no recommendations
        return CommandResult.success(recommendations.isEmpty() ? PRINT_EMPTY_LINE_TRIGGER : recommendations);
    }
}
