package net.justonedev.model;

import java.util.ArrayList;
import java.util.List;

public record RecommendationResult(boolean valid, String error, List<String> results) {
    public RecommendationResult(boolean valid, String error, List<String> results) {
        this.results = new ArrayList<>(results);
        this.valid = valid;
        this.error = error;
    }

    @Override
    public List<String> results() {
        return new ArrayList<>(results);
    }

    public static RecommendationResult valid(List<String> results) {
        return new RecommendationResult(true, "", results);
    }
    public static RecommendationResult invalid(String error) {
        return new RecommendationResult(false, error, List.of());
    }
}
