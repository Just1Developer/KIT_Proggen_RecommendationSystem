package net.justonedev.model.recommendation;

import net.justonedev.model.RecommendationResult;
import net.justonedev.model.RecommendationStrategy;
import net.justonedev.model.RecommendationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the result of a Nodeset construction, can be either successful or not. If successful,
 * will not contain any errors. If not successful, it will contain all errors that occurred during parsing.
 * @param nodeSet The set of nodes that was constructed.
 * @param errors The list of errors that occurred during construction. Empty if successful.
 * @author uwwfh
 */
public record ConstructSetResult(NodeSet nodeSet, List<String> errors) {
    /**
     * Regex is INTERSECTION(some string) or UNION(some string) with allowance for random spaces.
     */
    private static final Pattern SET_PATTERN = Pattern.compile("\\s*(INTERSECTION|UNION)\\s*\\(\\s*(.*)\\s*\\)");

    /**
     * Regex is first S1 / S2 / S3 for the type of recommendation strategy, and then the product number, with allowance for random spaces.
     */
    private static final Pattern SINGLE_SET_PATTERN = Pattern.compile("\\s*S([123])\\s+(\\d+)\\s*");

    private static final String ERROR_SET_INVALID_FORMAT = "The set definition (\"%s\") did not match the expected format";
    private static final String ERROR_SET_PARSE_ERROR = "Failed to parse numbers (impossible, because of regex validation)";
    private static final String MULTIPLE_ERRORS_FORMAT = "Multiple Errors: %s";
    private static final String MULTIPLE_ERRORS_DELIMITER = ", ";
    private static final char BRACKET_LEVEL_DEEPER = '(';
    private static final char BRACKET_LEVEL_SHALLOWER = ')';
    private static final char SET_FORMAT_DELIMITER = ',';

    private static final int GROUP_INDEX_STRATEGY = 1;
    private static final int GROUP_INDEX_PRODUCT_ID = 2;
    private static final int GROUP_INDEX_SET_OPERATION = 1;
    private static final int GROUP_INDEX_SET_OP_ARGS = 2;

    private static final int FIRST_SUBSTRING_BEGIN = 0;
    private static final int SECOND_SUBSTRING_STATIC_OFFSET = 1;

    private static final int STARTER_BRACKET_LEVEL = 0;
    private static final int TARGET_BRACKET_LEVEL = STARTER_BRACKET_LEVEL;
    private static final int NO_SEPARATING_COMMA = -1;

    private static final int SINGLE_ERROR_LIST_SIZE = 1;
    private static final int SINGLE_ERROR_LIST_INDEX = 0;

    /**
     * If the result is invalid, so if there are any errors present.
     * @return true if the result is invalid, false if it's valid.
     */
    public boolean isInvalid() {
        return !errors.isEmpty();
    }

    /**
     * Returns all errors, listed as a single-line string, separated with a comma.
     * @return all errors as one string.
     */
    public String getErrors() {
        return errors.size() == SINGLE_ERROR_LIST_SIZE ? errors.get(SINGLE_ERROR_LIST_INDEX)
                : MULTIPLE_ERRORS_FORMAT.formatted(String.join(MULTIPLE_ERRORS_DELIMITER, errors));
    }

    /**
     * Gets all recommendations, so all nodes, listed as a single-line string, properly formatted.
     * The recommendations are sorted by product ID.
     * @return all recommendations as formatted single-line string.
     */
    public String getRecommendations() {
        return nodeSet.getValue();
    }

    /**
     * Recursively parses and constructs a set of nodes from a string. If the string is invalid,
     * will return an invalid construction set. This can be checked using {@link ConstructSetResult#isInvalid()}.
     * @param system The current recommendation system that stores the data.
     * @param string The set construction as string, formatted as specified in the spec.
     * @return A ConstructSetResult containing either the Nodeset and no errors, or containing errors and some set of nodes.
     */
    public static ConstructSetResult constructSet(RecommendationSystem system, String string) {
        Matcher matcher = SET_PATTERN.matcher(string);
        Matcher singleSetMatcher = SINGLE_SET_PATTERN.matcher(string);
        if (!matcher.matches()) {
            // Single or nothing
            if (!singleSetMatcher.matches()) {
                return ConstructSetResult.failure(ERROR_SET_INVALID_FORMAT.formatted(string));
            }
            // Group 1 is guaranteed to be 1, 2 or 3
            try {
                RecommendationStrategy recommendationStrategy = RecommendationStrategy
                        .fromId(Integer.parseInt(singleSetMatcher.group(GROUP_INDEX_STRATEGY)));
                int productId = Integer.parseInt(singleSetMatcher.group(GROUP_INDEX_PRODUCT_ID));
                RecommendationResult result = system.getRecommendations(productId, recommendationStrategy);
                return result.valid() ? ConstructSetResult.success(new NodeSet(result.recommendations()))
                        : ConstructSetResult.failure(result.error());
            } catch (NumberFormatException e) {
                ConstructSetResult.failure(ERROR_SET_PARSE_ERROR);
            }
        }

        CombinationStrategy combinationStrategy = CombinationStrategy.valueOf(matcher.group(GROUP_INDEX_SET_OPERATION));
        String match = matcher.group(GROUP_INDEX_SET_OP_ARGS);
        int separator = findSeparatingComma(match);
        String firstSetMatch = match.substring(FIRST_SUBSTRING_BEGIN, separator).trim();
        String secondSetMatch = match.substring(separator + SECOND_SUBSTRING_STATIC_OFFSET).trim();

        ConstructSetResult firstSet = constructSet(system, firstSetMatch);
        ConstructSetResult secondSet = constructSet(system, secondSetMatch);

        if (firstSet.isInvalid() || secondSet.isInvalid()) {
            return ConstructSetResult.failure(firstSet, secondSet);
        }

        firstSet.nodeSet().combine(secondSet.nodeSet(), combinationStrategy);
        return firstSet;
    }

    private static ConstructSetResult success(NodeSet results) {
        return new ConstructSetResult(results, new ArrayList<>());
    }

    private static ConstructSetResult failure(String error) {
        return new ConstructSetResult(new NodeSet(RecommendationSystem.NO_DATA), new ArrayList<>(List.of(error)));
    }

    private static ConstructSetResult failure(ConstructSetResult previousErrors, ConstructSetResult morePreviousErrors) {
        List<String> errors = new ArrayList<>(previousErrors.errors);
        errors.addAll(morePreviousErrors.errors);
        return new ConstructSetResult(new NodeSet(RecommendationSystem.NO_DATA), errors);
    }

    private static int findSeparatingComma(String set) {
        int bracketLevel = STARTER_BRACKET_LEVEL;
        char[] chars = set.toCharArray();
        for (int i = FIRST_SUBSTRING_BEGIN; i < chars.length; i++) {
            if (chars[i] == BRACKET_LEVEL_DEEPER) {
                bracketLevel++;
            } else if (chars[i] == BRACKET_LEVEL_SHALLOWER) {
                bracketLevel--;
            } else if (bracketLevel == TARGET_BRACKET_LEVEL && chars[i] == SET_FORMAT_DELIMITER) {
                return i;
            }
        }
        return NO_SEPARATING_COMMA;
    }
}
