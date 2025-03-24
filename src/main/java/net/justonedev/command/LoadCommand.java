package net.justonedev.command;

import net.justonedev.model.Parser;
import net.justonedev.model.ParseGraphResult;
import net.justonedev.model.RecommendationSystem;
import net.justonedev.model.stream.DataStream;

import java.util.List;
import java.util.StringJoiner;

/**
 * The load command attempts to load a database graph from a specified file. The command prints the file contents
 * regardless of success/failure.
 * @author uwwfh
 */
public class LoadCommand implements Command {
    @Override
    public CommandResult execute(RecommendationSystem system, String[] args) {
        if (args.length < 2) {
            return CommandResult.failure("Not enough arguments: Required \"load database <filepath>\"");
        }
        String filePath = getFilepath(args);
        ParseGraphResult parseGraphResult = Parser.parseGraph(filePath);
        if (parseGraphResult.graph() != null) {
            system.loadGraph(parseGraphResult.graph());
            return CommandResult.success(formatLines(parseGraphResult.fileLines()));
        }
        return CommandResult.failure("Failed to parse database from file (Path: %s)".formatted(filePath),
                formatLines(parseGraphResult.fileLines()));
    }

    private static String formatLines(List<String> lines) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        DataStream.of(lines).forEach(joiner::add);
        return joiner.toString();
    }

    private static String getFilepath(String[] args) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 1; i < args.length; i++) {
            joiner.add(args[i]);
        }
        return joiner.toString();
    }
}
