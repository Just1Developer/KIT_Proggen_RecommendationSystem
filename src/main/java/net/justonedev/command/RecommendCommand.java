package net.justonedev.command;

import net.justonedev.model.FilterStrategy;
import net.justonedev.model.RecommendationResult;
import net.justonedev.model.RecommendationSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecommendCommand implements Command {
    private static final Pattern SET_PATTERN = Pattern.compile("\\s*(INTERSECTION|UNION)\\s*\\(\\s*(.*)\\s*\\)");
    private static final Pattern SINGLE_SET_PATTERN = Pattern.compile("\\s*S([123])\\s+(\\d+)\\s*");

    private static final int GROUP_INDEX_STRATEGY = 1;
    private static final int GROUP_INDEX_PRODUCT_ID = 2;
    private static final int GROUP_INDEX_SET_OPERATION = 1;
    private static final int GROUP_INDEX_SET_OP_ARGS = 2;

    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (!system.hasDatabase()) {
            return CommandResult.failure("No database has been loaded");
        }
        String setPattern = String.join(" ", args);
        ConstructSetResult constructSetResult = constructSet(system, setPattern);
        if (constructSetResult.isInvalid()) {
            return CommandResult.failure(constructSetResult.getErrors());
        }
        String recommendations = constructSetResult.getRecommendations();
        // Space such that an empty line is printed when there are no recommendations
        return CommandResult.success(recommendations.isEmpty() ? " " : recommendations);
    }

    private static ConstructSetResult constructSet(RecommendationSystem system, String string) {
        Matcher matcher = SET_PATTERN.matcher(string);
        Matcher singleSetMatcher = SINGLE_SET_PATTERN.matcher(string);
        if (!matcher.matches()) {
            // Single or nothing
            if (!singleSetMatcher.matches()) {
                return ConstructSetResult.failure("The set definition (\"%s\") did not match the expected format".formatted(string));
            }
            // Group 1 is guaranteed to be 1, 2 or 3
            FilterStrategy filterStrategy = FilterStrategy.fromId(Integer.parseInt(singleSetMatcher.group(GROUP_INDEX_STRATEGY)));
            int productId = Integer.parseInt(singleSetMatcher.group(GROUP_INDEX_PRODUCT_ID));
            RecommendationResult result = system.getRecommendations(productId, filterStrategy);
            return result.valid() ? ConstructSetResult.success(NodeSet.single(result.results())) : ConstructSetResult.failure(result.error());
        }

        CombinationStrategy combinationStrategy = CombinationStrategy.valueOf(matcher.group(GROUP_INDEX_SET_OPERATION));
        String match = matcher.group(GROUP_INDEX_SET_OP_ARGS);
        int separator = findSeparatingComma(match);
        String firstSetMatch = match.substring(0, separator).trim();
        String secondSetMatch = match.substring(separator + 1).trim();

        ConstructSetResult firstSet = constructSet(system, firstSetMatch);
        ConstructSetResult secondSet = constructSet(system, secondSetMatch);

        if (firstSet.isInvalid() || secondSet.isInvalid()) return ConstructSetResult.failure(firstSet, secondSet);

        firstSet.nodeSet().combine(secondSet.nodeSet(), combinationStrategy);
        return firstSet;
    }

    private static int findSeparatingComma(String set) {
        int bracketLevel = 0;
        char[] chars = set.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '(') bracketLevel++;
            else if (chars[i] == ')') bracketLevel--;
            else if (bracketLevel == 0 && chars[i] == ',') return i;
        }
        return -1;
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

            public void combine(NodeSet other, CombinationStrategy strategy) {
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

            public static NodeSet single(List<String> nodes) {
                return new NodeSet(nodes);
            }

            public String getValue() {
                return String.join(" ", nodes.stream().distinct().sorted().toList());
            }
        }

    private enum CombinationStrategy {
        SINGLE,
        INTERSECTION,
        UNION
    }

    private record ConstructSetResult(NodeSet nodeSet, List<String> errors) {
        public boolean isInvalid() {
            return !errors.isEmpty();
        }
        public String getErrors() {
            return errors.size() == 1 ? errors.getFirst() : "Multiple Errors: %s".formatted(String.join(", ", errors));
        }
        public String getRecommendations() {
            return nodeSet.getValue();
        }

        public static ConstructSetResult success(NodeSet results) {
            return new ConstructSetResult(results, new ArrayList<>());
        }
        public static ConstructSetResult failure(String error) {
            return new ConstructSetResult(NodeSet.single(List.of()), new ArrayList<>(List.of(error)));
        }

        public static ConstructSetResult failure(ConstructSetResult previousErrors, ConstructSetResult morePreviousErrors) {
            List<String> errors = new ArrayList<>(previousErrors.errors);
            errors.addAll(morePreviousErrors.errors);
            return new ConstructSetResult(NodeSet.single(List.of()), errors);
        }
    }
}
