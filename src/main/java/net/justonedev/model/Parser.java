package net.justonedev.model;

import net.justonedev.command.CommandResult;
import net.justonedev.command.ResultType;
import net.justonedev.model.graph.EdgeData;
import net.justonedev.model.graph.EdgeType;
import net.justonedev.model.graph.DatabaseGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The parser provides methods for parsing graph files and edge data from strings.
 * @author uwwfh
 */
public final class Parser {
    /**
     * Regex that combines an edge with capturing groups for node names, their product ids (optional) and every connection type.
     */
    private static final Pattern EDGE_PATTERN = Pattern.compile("([a-zA-Z\\d]+)(?:\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\))?\\s+"
            + "(contained-in|contains|(?:suc|prede)cessor-of|part-of|has-part)\\s+([a-zA-Z\\d]+)(?:\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\))?");

    private static final String ERROR_FILE_READING = "Failed to read file: %s";
    private static final String ERROR_PARSER = "An error occurred while parsing the database. The database will not be loaded.";

    private static final int EDGE_GROUP_INDEX_FROM_NAME = 1;
    private static final int EDGE_GROUP_INDEX_FROM_ID = 2;
    private static final int EDGE_GROUP_INDEX_EDGE_TYPE = 3;
    private static final int EDGE_GROUP_INDEX_TO_NAME = 4;
    private static final int EDGE_GROUP_INDEX_TO_ID = 5;

    private Parser() { }

    /**
     * Parses a graph from a given file. Returns a result wrapper with validity status, Graph (maybe null), and,
     * if invalid, an error message.
     * @param file The filepath.
     * @return The result of the graph parsing.
     */
    public static ParseGraphResult parseGraph(String file) {
        DatabaseGraph databaseGraph = new DatabaseGraph();
        Optional<List<String>> lineOptional = readFile(file);
        if (lineOptional.isEmpty()) {
            return ParseGraphResult.failure(RecommendationSystem.NO_DATA, ERROR_FILE_READING.formatted(file));
        }
        boolean isValid = true;
        for (String line : lineOptional.get()) {
            Optional<EdgeData> data = parseEdge(line);
            if (data.isPresent()) {
                CommandResult result = databaseGraph.addEdge(data.get());
                if (result.resultType() == ResultType.FAILURE) {
                    isValid = false;
                }
            }
            if (data.isEmpty()) {
                isValid = false;
            }
        }
        return isValid ? ParseGraphResult.success(databaseGraph, lineOptional.get()) : ParseGraphResult.failure(lineOptional.get(),
                ERROR_PARSER);
    }

    /**
     * Parses an edge from a string and returns edge data. Returns an empty optional if the
     * edge was invalid, with no error message.
     * @param line The line to parse to an edge.
     * @return The optional edge data.
     */
    public static Optional<EdgeData> parseEdge(String line) {
        Matcher matcher = EDGE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        String fromNodeName = matcher.group(EDGE_GROUP_INDEX_FROM_NAME).toLowerCase();
        EdgeType type = EdgeType.getEdgeType(matcher.group(EDGE_GROUP_INDEX_EDGE_TYPE));
        String toNodeName = matcher.group(EDGE_GROUP_INDEX_TO_NAME).toLowerCase();
        int fromNodeId;
        int toNodeId;
        try {
            String fromIdMatch = matcher.group(EDGE_GROUP_INDEX_FROM_ID);
            String toIdMatch = matcher.group(EDGE_GROUP_INDEX_TO_ID);
            fromNodeId = fromIdMatch == null || fromIdMatch.isBlank() ? DatabaseGraph.PROGRAM_ID_CATEGORY : Integer.parseInt(fromIdMatch);
            toNodeId = toIdMatch == null || toIdMatch.isBlank() ? DatabaseGraph.PROGRAM_ID_CATEGORY : Integer.parseInt(toIdMatch);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(new EdgeData(fromNodeName, fromNodeId, toNodeName, toNodeId, type));
    }

    /**
     * Reads a file and returns the contents, line by line, in a String array.
     * Empty lines are empty String entries in the array.
     * <br/>
     * The filepath can be relative, but the File constructor must find it
     * using new File(filepath);
     * <br/>
     * Returns an empty array if the file is not found. Prints an error to the
     * Default Error Stream {@code System.err}.
     * <br/>
     * FROM: <a href="https://s.justonedev.net/filereader">JustOneDev's FileReader</a>
     *
     * @param filepath The filepath. May be relative.
     * @return An array of the lines. Not null.
     */
    private static Optional<List<String>> readFile(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
