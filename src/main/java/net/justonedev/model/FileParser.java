package net.justonedev.model;

import net.justonedev.model.g.EdgeData;
import net.justonedev.model.g.EdgeType;
import net.justonedev.model.g.DatabaseGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileParser {
    private static final Pattern EDGE_PATTERN = Pattern.compile("([a-zA-Z\\d]+)(?:\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\))?\\s+(contained-in|contains|(?:suc|prede)cessor-of|part-of|has-part)\\s+([a-zA-Z\\d]+)(?:\\s*\\(\\s*id\\s*=\\s*(\\d+)\\s*\\))?");

    private FileParser() { }

    public static ParseGraphResult parseGraph(String file) {
        DatabaseGraph databaseGraph = new DatabaseGraph();
        Optional<List<String>> lineOptional = readFile(file);
        if (lineOptional.isEmpty()) {
            return ParseGraphResult.empty();
        }
        boolean isValid = true;
        for (String line : lineOptional.get()) {
            Optional<EdgeData> data = parseEdge(line);
            data.ifPresent(databaseGraph::addEdge);
            if (data.isEmpty()) isValid = false;
        }
        return isValid ? new ParseGraphResult(Optional.of(databaseGraph), lineOptional.get()) : ParseGraphResult.empty();
    }

    public static Optional<EdgeData> parseEdge(String line) {
        Matcher matcher = EDGE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        String fromNodeName = matcher.group(1).toLowerCase();
        EdgeType type = EdgeType.getEdgeType(matcher.group(3));
        String toNodeName = matcher.group(4).toLowerCase();
        int fromNodeId, toNodeId;
        try {
            String fromIdMatch = matcher.group(2);
            String toIdMatch = matcher.group(5);
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
        // Find the file
        File file = new File(filepath);
        if (!file.exists()) {
            System.err.printf("Unable to find File %s%n", filepath);
            return Optional.empty();
        }

        // Open a scanner
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.err.printf("Failed to create FileInputStream for File %s%n", filepath);
            return Optional.empty();
        }

        // Read file and save Strings in list
        List<String> lines = new ArrayList<>();
        for (String s; scanner.hasNext() && (s = scanner.nextLine()) != null;) {
            lines.add(s);
        }
        return Optional.of(lines);
    }
}
