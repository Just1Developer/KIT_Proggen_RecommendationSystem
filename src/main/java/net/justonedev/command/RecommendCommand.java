package net.justonedev.command;

import net.justonedev.model.RecommendationStrategy;
import net.justonedev.model.RecommendationResult;
import net.justonedev.model.RecommendationSystem;
import net.justonedev.model.stream.DataStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The recommend command gives recommendations for a given product id using specified recommendation strategies (three available).
 * The command also allows for combination of these recommendation strategies using set intersection and set union.
 * @author uwwfh
 */
public class RecommendCommand implements Command {

    /**
     * Regex is INTERSECTION(some string) or UNION(some string) with allowance for random spaces.
     */
    private static final Pattern SET_PATTERN = Pattern.compile("\\s*(INTERSECTION|UNION)\\s*\\(\\s*(.*)\\s*\\)");

    /**
     * Regex is first S1 / S2 / S3 for the type of recommendation strategy, and then the product number, with allowance for random spaces.
     */
    private static final Pattern SINGLE_SET_PATTERN = Pattern.compile("\\s*S([123])\\s+(\\d+)\\s*");

    private static final String PRINT_EMPTY_LINE_TRIGGER = " ";
    private static final String ERROR_SET_INVALID_FORMAT = "The set definition (\"%s\") did not match the expected format";
    private static final String ERROR_SET_PARSE_ERROR = "Failed to parse numbers (impossible, because of regex validation)";
    private static final String PRODUCT_ID_FORMAT_REGEX = ":\\d+";
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

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (!system.hasDatabase()) {
            return CommandResult.failure(RecommendationSystem.GRAPH_NOT_EXISTS);
        }
        String setPattern = String.join(CommandHandler.ARGUMENT_DELIMITER, args);
        ConstructSetResult constructSetResult = constructSet(system, setPattern);
        if (constructSetResult.isInvalid()) {
            return CommandResult.failure(constructSetResult.getErrors());
        }
        String recommendations = constructSetResult.getRecommendations();
        // Space such that an empty line is printed when there are no recommendations
        return CommandResult.success(recommendations.isEmpty() ? PRINT_EMPTY_LINE_TRIGGER : recommendations);
    }

    private static ConstructSetResult constructSet(RecommendationSystem system, String string) {
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
                return result.valid() ? ConstructSetResult.success(NodeSet.single(result.recommendations()))
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

    private record NodeSet(List<String> nodes) {
        private NodeSet(List<String> nodes) {
            this.nodes = new ArrayList<>(nodes);
        }

        private void join(NodeSet other) {
            nodes.addAll(other.nodes);
        }

        private void intersect(NodeSet other) {
            nodes.removeIf(node -> !other.nodes.contains(node));
        }

        private void combine(NodeSet other, CombinationStrategy strategy) {
            switch (strategy) {
                case INTERSECTION:
                    intersect(other);
                    break;
                case UNION:
                    join(other);
                    break;
                default:
                    break;
            }
        }

        private static NodeSet single(List<String> nodes) {
            return new NodeSet(nodes);
        }

        private String getValue() {
            return String.join(RecommendationSystem.INLINE_LIST_DELIMITER, DataStream.of(nodes).distinct().sorted(node ->
                    node.replaceAll(PRODUCT_ID_FORMAT_REGEX, "")).toList());
        }
    }

    private enum CombinationStrategy {
        SINGLE,
        INTERSECTION,
        UNION
    }

    private record ConstructSetResult(NodeSet nodeSet, List<String> errors) {
        private boolean isInvalid() {
            return !errors.isEmpty();
        }
        private String getErrors() {
            return errors.size() == SINGLE_ERROR_LIST_SIZE ? errors.get(SINGLE_ERROR_LIST_INDEX)
                    : MULTIPLE_ERRORS_FORMAT.formatted(String.join(MULTIPLE_ERRORS_DELIMITER, errors));
        }
        private String getRecommendations() {
            return nodeSet.getValue();
        }

        private static ConstructSetResult success(NodeSet results) {
            return new ConstructSetResult(results, new ArrayList<>());
        }
        private static ConstructSetResult failure(String error) {
            return new ConstructSetResult(NodeSet.single(RecommendationSystem.NO_DATA), new ArrayList<>(List.of(error)));
        }

        private static ConstructSetResult failure(ConstructSetResult previousErrors, ConstructSetResult morePreviousErrors) {
            List<String> errors = new ArrayList<>(previousErrors.errors);
            errors.addAll(morePreviousErrors.errors);
            return new ConstructSetResult(NodeSet.single(RecommendationSystem.NO_DATA), errors);
        }
    }
}
