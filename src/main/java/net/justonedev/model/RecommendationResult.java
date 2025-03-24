package net.justonedev.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper class for recommendation recommendations. Stores if the result is valid, the error if it's not, and
 * the list of recommendations if it is.<br/>
 * If the result is invalid, the recommendations will be an empty list.
 *
 * @author uwwfh
 *
 * @param valid If the result is valid.
 * @param error The error message if it is not valid.
 * @param recommendations The list of recommendation recommendations.
 */
public record RecommendationResult(boolean valid, String error, List<String> recommendations) {
    /**
     * Creates a new recommendation result. Overrides the constructor because otherwise
     * we would be assigning a reference to list fields.
     * @param recommendations The recommendations of the recommendation.
     * @param valid If the graph is valid.
     * @param error The error message if it isn't.
     */
    public RecommendationResult(boolean valid, String error, List<String> recommendations) {
        this.recommendations = new ArrayList<>(recommendations);
        this.valid = valid;
        this.error = error;
    }

    /**
     * Gets the recommendations. Override because of list reference returns.
     * @return A copy of the recommendations.
     */
    public List<String> recommendations() {
        return new ArrayList<>(recommendations);
    }

    /**
     * Creates a new valid recommendation result of the given recommendations.
     * @param results The recommendations.
     * @return A valid recommendation result of the recommendations.
     */
    public static RecommendationResult valid(List<String> results) {
        return new RecommendationResult(true, "", results);
    }

    /**
     * Creates a new invalid recommendation result with a specified error message.
     * @param error The error message.
     * @return An invalid recommendation result with the given error message.
     */
    public static RecommendationResult invalid(String error) {
        return new RecommendationResult(false, error, RecommendationSystem.NO_DATA);
    }
}
